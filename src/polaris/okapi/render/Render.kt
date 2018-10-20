package polaris.okapi.render

import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL33C
import org.lwjgl.opengl.GL33C.*
import org.lwjgl.system.rpmalloc.RPmalloc
import polaris.okapi.util.readFileAsString
import java.io.File
import java.nio.FloatBuffer

enum class VertexAttributes(val ids: IntArray, val strides: IntArray, val strideLength: Int, val offsets: LongArray, val enable: (() -> Unit), val pointer: (() -> Unit)) {
    POS(intArrayOf(0), intArrayOf(3), 3 * 4, longArrayOf(0), {
        glEnableVertexAttribArray(0)
    }, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0)
    }),
    POS_COLOR(intArrayOf(0, 1), intArrayOf(3, 4), (3 + 4) * 4, longArrayOf(0, 3 * 4), {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
    }, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 + 4) * 4, 0)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, (3 + 4) * 4, 3 * 4)
    }),
    POS_TEXTURE(intArrayOf(0, 3), intArrayOf(3, 2), (3 + 2) * 4, longArrayOf(0, 3 * 4), {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(3)
    }, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 + 2) * 4, 0)
        glVertexAttribPointer(3, 2, GL_FLOAT, false, (3 + 2) * 4, 3 * 4)
    }),
    POS_COLOR_TEXTURE(intArrayOf(0, 1, 3), intArrayOf(3, 4, 2), (3 + 4 + 2) * 4, longArrayOf(0, 3 * 4, 7 * 4), {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(3)
    }, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 + 4 + 2) * 4, 0)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, (3 + 4 + 2) * 4, 3 * 4)
        glVertexAttribPointer(3, 2, GL_FLOAT, false, (3 + 4 + 2) * 4, 7 * 4)
    }),
    POS_NORMAL_TEXTURE(intArrayOf(0, 2, 3), intArrayOf(3, 3, 2), (3 + 3 + 2) * 4, longArrayOf(0, 3 * 4, 6 * 4), {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(2)
        glEnableVertexAttribArray(3)
    }, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 + 3 + 2) * 4, 0)
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (3 + 3 + 2) * 4, 3 * 4)
        glVertexAttribPointer(3, 2, GL_FLOAT, false, (3 + 3 + 2) * 4, 6 * 4)
    }),
    POS_COLOR_NORMAL_TEXTURE(intArrayOf(0, 1, 2, 3), intArrayOf(3, 4, 3, 2), (3 + 4 + 3 + 2) * 4, longArrayOf(0, 3 * 4, 7 * 4, 10 * 4), {
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)
        glEnableVertexAttribArray(3)
    }, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 + 4 + 3 + 2) * 4, 0)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, (3 + 4 + 3 + 2) * 4, 3 * 4)
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (3 + 4 + 3 + 2) * 4, 7 * 4)
        glVertexAttribPointer(3, 2, GL_FLOAT, false, (3 + 4 + 3 + 2) * 4, 10 * 4)
    });
}

class DrawArray(private val glMode: Int, private val vboDrawMode: Int, floatArray: FloatArray, private val verticeCount: Int, private val attributes: VertexAttributes) {

    val vaoId: Int = glGenVertexArrays()
    val vboId: Int = glGenBuffers()

    var buffer: FloatBuffer = RPmalloc.rpmalloc((verticeCount * attributes.strideLength).toLong())!!.asFloatBuffer()

    var array: FloatArray = floatArray
        set(value) {
            buffer.clear()
            buffer.put(value)
            buffer.flip()

            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, vboId)
            GL33C.glBufferSubData(GL33C.GL_ARRAY_BUFFER, 0, buffer)

            field = value
        }

    init {
        buffer.put(array)
        create()
    }

    fun create() {
        bind()

        buffer.flip()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, buffer, vboDrawMode)

        attributes.enable()
        attributes.pointer()

        disable()
    }

    fun bind() {
        glBindVertexArray(vaoId)
    }

    fun disable() {
        glBindVertexArray(0)
    }

    fun draw() {
        glDrawArrays(glMode, 0, verticeCount)
    }

    fun destroy() {
        RPmalloc.rpfree(buffer)
        glDeleteBuffers(vboId)

        glDeleteVertexArrays(vaoId)
    }

}

class DrawElement(private val glMode: Int, private val vboDrawMode: Int, private val verticeCount: Int, private val attributes: VertexAttributes) {

    val vaoId: Int = glGenVertexArrays()
    val vboId: Int = glGenBuffers()
    val iboId: Int = glGenBuffers()
    var buffer: FloatBuffer = RPmalloc.rpmalloc((verticeCount * attributes.strideLength).toLong())!!.asFloatBuffer()

    constructor(glMode: Int, vboDrawMode: Int, array: FloatArray, verticeCount: Int, attributes: VertexAttributes) : this(glMode, vboDrawMode, verticeCount, attributes) {
        buffer.put(array)
        create()
    }

    constructor(glMode: Int, vboDrawMode: Int, buffer: FloatBuffer, verticeCount: Int, attributes: VertexAttributes) : this(glMode, vboDrawMode, verticeCount, attributes) {
        buffer.put(buffer)
        create()
    }

    fun create() {
        bind()

        buffer.flip()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, buffer, vboDrawMode)

        for (i in attributes.ids) {
            glEnableVertexAttribArray(i)
            glVertexAttribPointer(i, attributes.strides[i], GL_FLOAT, false, attributes.strideLength, attributes.offsets[i])
        }

        disable()
    }

    fun bind() {
        glBindVertexArray(vaoId)
    }

    fun disable() {
        glBindVertexArray(0)
    }

    fun draw() {
        glDrawArrays(glMode, 0, verticeCount)
    }

    fun destroy() {
        RPmalloc.rpfree(buffer)
        glDeleteBuffers(vboId)

        glDeleteVertexArrays(vaoId)
    }

}

class Shader {
    val shaderId: Int = glCreateProgram()

    var vertexShaderId: Int = 0
    var fragmentShaderId: Int = 0

    operator fun set(shaderType: Int, value: String) {
        if(shaderType == GL_VERTEX_SHADER)
            vertexShaderId = loadShader(File(value), shaderType)
        else if(shaderType == GL_FRAGMENT_SHADER)
            fragmentShaderId = loadShader(File(value), shaderType)
    }

    operator fun get(loc: String): Int {
        return glGetUniformLocation(shaderId, loc)
    }

    fun link() {

        glAttachShader(shaderId, vertexShaderId)
        glAttachShader(shaderId, fragmentShaderId)

        glLinkProgram(shaderId)
        if(glGetProgrami(shaderId, GL_LINK_STATUS) == GL_FALSE)
            throw RuntimeException("Unable to link shader program:" + GL20C.glGetProgramInfoLog(shaderId))

    }

    fun bind() {
        glUseProgram(shaderId)
    }

    fun disable() {
        glUseProgram(0)
    }

    fun destroy() {
        disable()

        glDetachShader(shaderId, vertexShaderId)
        glDetachShader(shaderId, fragmentShaderId)

        glDeleteShader(vertexShaderId)
        glDeleteShader(fragmentShaderId)

        glDeleteProgram(shaderId)
    }

    private fun loadShader(filename: File, shaderType: Int): Int {
        var shader = 0
        try {
            shader = GL20C.glCreateShader(shaderType)

            if (shader == 0) return 0

            GL20C.glShaderSource(shader, readFileAsString(filename))

            GL20C.glCompileShader(shader)

            if (GL20C.glGetShaderi(shader, GL20C.GL_COMPILE_STATUS) == GL20C.GL_FALSE)
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))

            return shader
        } catch (exc: Exception) {
            if(shader != 0)
                GL20C.glDeleteShader(shader)

            println(exc.message)
            return -1
        }

    }

    private fun getLogInfo(obj: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB))
    }


}