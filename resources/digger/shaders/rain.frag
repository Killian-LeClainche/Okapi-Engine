uniform float time;
uniform vec2 resolution;
uniform float hue;
uniform float fade;
uniform float slow;
uniform float gray;

// Created by Reinder Nijhoff 2014
// @reindernijhoff
//
// https://www.shadertoy.com/view/Xtf3zn
//
// car model is made by Eiffie
// shader 'Shiny Toy': https://www.shadertoy.com/view/ldsGWB

#define BUMPMAP
#define MARCHSTEPS 128
#define MARCHSTEPSREFLECTION 48
#define LIGHTINTENSITY 5.

//----------------------------------------------------------------------

const vec3 backgroundColor = vec3(0.2,0.4,0.6) * 0.0;

//----------------------------------------------------------------------
// noises

float hash( float n ) {
    return fract(sin(n)*687.3123);
}

float noise( in vec2 x ) {
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float n = p.x + p.y*157.0;
    return mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
               mix( hash(n+157.0), hash(n+158.0),f.x),f.y);
}

const mat2 m2 = mat2( 0.80, -0.60, 0.60, 0.80 );

float fbm( vec2 p ) {
    float f = 0.0;
    f += 0.5000*noise( p ); p = m2*p*2.02;
    f += 0.2500*noise( p ); p = m2*p*2.03;
    f += 0.1250*noise( p ); p = m2*p*2.01;
//    f += 0.0625*noise( p );

    return f/0.9375;
}

//----------------------------------------------------------------------
// distance primitives

float udRoundBox( vec3 p, vec3 b, float r ) {
  return length(max(abs(p)-b,0.0))-r;
}

float sdBox( in vec3 p, in vec3 b ) {
    vec3 d = abs(p) - b;
    return min(max(d.x,max(d.y,d.z)),0.0) + length(max(d,0.0));
}

float sdSphere( in vec3 p, in float s ) {
    return length(p)-s;
}

float sdCylinder( in vec3 p, in vec2 h ) {
    vec2 d = abs(vec2(length(p.xz),p.y)) - h;
    return min(max(d.x,d.y),0.0) + length(max(d,0.0));
}

//----------------------------------------------------------------------
// distance operators

float opU( float d2, float d1 ) { return min( d1,d2); }
float opS( float d2, float d1 ) { return max(-d1,d2); }
float smin( float a, float b, float k ) { return -log(exp(-k*a)+exp(-k*b))/k; } //from iq

//----------------------------------------------------------------------
// Map functions

// car model is made by Eiffie
// shader 'Shiny Toy': https://www.shadertoy.com/view/ldsGWB


float dL; // minimal distance to light

float map( const in vec3 p ) {
	vec3 pd = p;
    float d;

    pd.x = abs( pd.x );
    pd.z *= -sign( p.x );

    float ch = hash( floor( (pd.z+18.*time)/40. ) );
    float lh = hash( floor( pd.z/13. ) );

    vec3 pdm = vec3( pd.x, pd.y, mod( pd.z, 10.) - 5. );
    dL = sdSphere( vec3(pdm.x-8.1,pdm.y-4.5,pdm.z), 0.1 );

    dL = opU( dL, sdBox( vec3(pdm.x-12., pdm.y-9.5-lh,  mod( pd.z, 91.) - 45.5 ), vec3(0.2,4.5, 0.2) ) );
    dL = opU( dL, sdBox( vec3(pdm.x-12., pdm.y-11.5+lh, mod( pd.z, 31.) - 15.5 ), vec3(0.22,5.5, 0.2) ) );
    dL = opU( dL, sdBox( vec3(pdm.x-12., pdm.y-8.5-lh,  mod( pd.z, 41.) - 20.5 ), vec3(0.24,3.5, 0.2) ) );

    if( lh > 0.5 ) {
	    dL = opU( dL, sdBox( vec3(pdm.x-12.5,pdm.y-2.75-lh,  mod( pd.z, 13.) - 6.5 ), vec3(0.1,0.25, 3.2) ) );
    }

    vec3 pm = vec3( mod( pd.x + floor( pd.z * 4. )*0.25, 0.5 ) - 0.25, pd.y, mod( pd.z, 0.25 ) - 0.125 );
	d = udRoundBox( pm, vec3( 0.245,0.1, 0.12 ), 0.005 );

    d = opS( d, -(p.x+8.) );
    d = opU( d, pd.y );

    vec3 pdc = vec3( pd.x, pd.y, mod( pd.z+18.*time, 40.) - 20. );


    d = opU( d, 13.-pd.x );
    d = opU( d, sdCylinder( vec3(pdm.x-8.5, pdm.y, pdm.z), vec2(0.075,4.5)) );
    d = opU( d, dL );

	return d;
}

//----------------------------------------------------------------------

vec3 calcNormalSimple( in vec3 pos ) {
    const vec2 e = vec2(1.0,-1.0)*0.005;

    vec3 n = normalize( e.xyy*map( pos + e.xyy ) +
					    e.yyx*map( pos + e.yyx )   +
					    e.yxy*map( pos + e.yxy )   +
					    e.xxx*map( pos + e.xxx )   );
    return n;
}

vec3 calcNormal( in vec3 pos ) {
    vec3 n = calcNormalSimple( pos );
    if( pos.y > 0.12 ) return n;

#ifdef BUMPMAP
    vec2 oc = floor( vec2(pos.x+floor( pos.z * 4. )*0.25, pos.z) * vec2( 2., 4. ) );

    if( abs(pos.x)<8. ) {
		oc = pos.xz;
    }

     vec3 p = pos * 250.;
   	 vec3 xn = 0.05*vec3(noise(p.xz)-0.5,0.,noise(p.zx)-0.5);
     xn += 0.1*vec3(fbm(oc.xy)-0.5,0.,fbm(oc.yx)-0.5);

    n = normalize( xn + n );
#endif

    return n;
}


//----------------------------------------------------------------------
// shade


float randomStart(vec2 co){return 0.8+0.2*hash(dot(co,vec2(123.42,117.853))*412.453);}

//----------------------------------------------------------------------
// main

void main() {
    vec2 q = gl_FragCoord.xy / resolution.xy;
	vec2 p = -1.0 + 2.0 * q;
	p.x *= resolution.x / resolution.y;

    if (q.y < .12 || q.y >= .88) {
		gl_FragColor=vec4(0.,0.,0.,1.);
		return;
    } else {

        // camera
        float z = time;
        float x = -10.9+1.*sin(time*0.2);
        vec3 ro = vec3(x,  1.3+.3*cos(time*0.26), z-1.);
        vec3 ta = vec3(-8.,1.3+.4*cos(time*0.26), z+4.+cos(time*0.04));

        vec3 ww = normalize( ta - ro );
        vec3 uu = normalize( cross(ww,vec3(0.0,1.0,0.0) ) );
        vec3 vv = normalize( cross(uu,ww));
        vec3 rd = normalize( -p.x*uu + p.y*vv + 2.2*ww );

        vec3 col = backgroundColor;

        // Rain (by Dave Hoskins)
        vec2 st = 256. * ( p* vec2(.5, .01)+vec2(time*.13-q.y*.6, time*.13) );
        float f = noise( st ) * noise( st*0.773) * 1.55;
        f = 0.25+ clamp(pow(abs(f), 13.0) * 13.0, 0.0, q.y*.14);


        col += 0.25*f*(0.2+backgroundColor);

        // post processing
        col = pow( clamp(col,0.0,1.0), vec3(0.4545) );
        col *= 1.2*vec3(1.,0.99,0.95);
        col = clamp(1.06*col-0.03, 0., 1.);
        q.y = (q.y-.12)*(1./0.76);
        col *= 0.5 + 0.5*pow( 16.0*q.x*q.y*(1.0-q.x)*(1.0-q.y), 0.1 );

        gl_FragColor = vec4( col, 1.0 );
    }
}
