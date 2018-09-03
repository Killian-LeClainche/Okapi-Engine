package polaris.okapi.util

import polaris.okapi.App
import org.lwjgl.BufferUtils
import org.lwjgl.BufferUtils.createByteBuffer
import java.awt.image.BufferedImage
import java.io.*
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_ONLY
import java.nio.file.Paths
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.ArrayList
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import javax.imageio.ImageIO

val classLoader = App::class.java.classLoader!!

/**
 * Created by Killian Le Clainche on 12/12/2017.
 */

fun convertToBytes(list: List<Byte>): ByteArray {
    val array = ByteArray(list.size)
    for (i in array.indices) {
        array[i] = list[i]
    }
    return array
}

@Throws(IOException::class)
fun readCompressedFile(file: File): Array<String> {
    val stringList = ArrayList<String>()
    val stream = InflaterInputStream(FileInputStream(file), Inflater(), 1024)
    val reader = BufferedReader(InputStreamReader(stream))
    var line: String? = null

    reader.lines().forEach { stringList.add(it) }

    reader.close()
    return stringList.toTypedArray()
}

fun getResource(s: String): URL? {
    return classLoader.getResource(s)
}

fun getResourceStream(s: String): InputStream {
    return classLoader.getResourceAsStream(s)
}

@Throws(IOException::class)
fun downloadFile(url: String, dest: File) {
    var md5: String? = null
    if (dest.exists()) {
        md5 = getMD5(dest)
    }

    val connection = URL(url).openConnection() as HttpURLConnection
    if (md5 != null) {
        connection.setRequestProperty("If-None-Match", md5)
    }
    connection.connect()

    if (connection.responseCode == 304) {
        return
    }

    createFileSafely(dest)

    val input = BufferedInputStream(connection.inputStream, 16384)
    val out = BufferedOutputStream(FileOutputStream(dest), 16384)

    out.write(input.readBytes())

    out.flush()
    out.close()
    input.close()
    connection.disconnect()
}

@Throws(IOException::class)
fun getMD5(file: File): String? {
    var stream: DigestInputStream? = null
    try {
        stream = DigestInputStream(FileInputStream(file), MessageDigest.getInstance("MD5"))
        val buffer = ByteArray(65536)

        var read = stream.read(buffer)
        while (read >= 1) read = stream.read(buffer)
    } catch (ignored: Exception) {
        return null
    } finally {
        stream!!.close()
    }

    return String.format("%1$032x", *arrayOf<Any>(BigInteger(1, stream.messageDigest.digest())))
}

@Throws(IOException::class)
fun createFileSafely(file: File) {
    val parentFile = File(file.parent)
    if (!parentFile.exists()) {
        if (!parentFile.mkdirs()) {
            throw IOException("Unable to create parent file: " + file.parent)
        }
    }
    if (file.exists()) {
        if (!file.delete()) {
            throw IOException("Couldn't delete '" + file.absolutePath + "'")
        }
    }
    if (!file.createNewFile()) {
        throw IOException("Couldn't create '" + file.absolutePath + "'")
    }
}

fun downloadImage(url: String): BufferedImage? {
    var connection: HttpURLConnection? = null
    return try {
        connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val image = ImageIO.read(connection.inputStream)
        connection.disconnect()
        image
    } catch (e: IOException) {
        e.printStackTrace()
        if (connection != null) {
            connection.disconnect()
        }
        null
    }

}

@Throws(IOException::class)
fun readStreamFully(stream: InputStream): ByteArray {
    val data = ByteArray(4096)
    val entryBuffer = ByteArrayOutputStream()
    var len: Int
    do {
        len = stream.read(data)
        if (len > 0) {
            entryBuffer.write(data, 0, len)
        }
    } while (len != -1)

    return entryBuffer.toByteArray()
}

fun ioResourceToByteBuffer(file: File): ByteBuffer? {
    val fileStream = FileInputStream(file)
    val channel = fileStream.channel

    return try {
        channel.map(READ_ONLY, 0, channel.size())
    } catch(e: IOException) {
        System.err.println("Couldn't read I/O Resource!")
        System.err.println(e.message)
        null
    } finally {
        try {
            channel.close()
        }
        catch(e: IOException) {
            System.err.println("Couldn't close File Channel!")
            System.err.println(e.message)
        }
        try {
            fileStream.close()
        }
        catch(e: IOException) {
            System.err.println("Couldn't close File Stream!")
            System.err.println(e.message)
        }
    }
}

@Throws(IOException::class)
fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
    var buffer: ByteBuffer

    val url = Thread.currentThread().contextClassLoader.getResource(resource)
    val file: File
    file = if (url != null)
        File(url.file)
    else
        File(resource)
    if (file.isFile) {
        val fis = FileInputStream(file)
        val fc = fis.channel
        buffer = BufferUtils.createByteBuffer(fc.size().toInt() + 1)

        while (fc.read(buffer) != -1);

        fc.close()
        fis.close()
    } else {
        buffer = BufferUtils.createByteBuffer(bufferSize)

        val source = url!!.openStream() ?: throw FileNotFoundException(resource)

        source.use { _source ->
            val rbc = Channels.newChannel(_source)
            rbc.use { _rbc ->
                while (true) {
                    val bytes = _rbc.read(buffer)
                    if (bytes == -1) break
                    if (buffer.remaining() == 0) buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                }
            }
        }
    }

    buffer.flip()
    return buffer
}

@Throws(Exception::class)
fun readFileAsString(file: File): String {
    val source = StringBuilder()

    val `in` = FileInputStream(file)

    var exception: Exception? = null

    val reader: BufferedReader
    try {
        reader = BufferedReader(InputStreamReader(`in`, "UTF-8"))

        var innerExc: Exception? = null
        try {
            var line: String? = reader.readLine()
            while (line != null) {
                source.append(line).append('\n')
                line = reader.readLine()
            }
        } catch (exc: Exception) {
            exception = exc
        } finally {
            try {
                reader.close()
            } catch (exc: Exception) {
                if (innerExc == null)
                    innerExc = exc
                else
                    exc.printStackTrace()
            }

        }

        if (innerExc != null) throw innerExc
    } catch (exc: Exception) {
        exception = exc
    } finally {
        try {
            `in`.close()
        } catch (exc: Exception) {
            if (exception == null)
                exception = exc
            else
                exc.printStackTrace()
        }

        if (exception != null) throw exception
    }

    return source.toString()
}

fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
    val newBuffer = BufferUtils.createByteBuffer(newCapacity)
    buffer.flip()
    newBuffer.put(buffer)
    return newBuffer
}

@Throws(IOException::class)
fun copyFile(from: File, to: File) {
    createFileSafely(to)
    val bis = BufferedInputStream(FileInputStream(from))
    val bos = BufferedOutputStream(FileOutputStream(to))
    var block: ByteArray
    while (bis.available() > 0) {
        block = ByteArray(16384)
        val readNow = bis.read(block)
        bos.write(block, 0, readNow)
    }
    bos.flush()
    bos.close()
    bis.close()
}

@Throws(FileNotFoundException::class)
fun newReader(file: File): BufferedReader {
    return BufferedReader(FileReader(file))
}

@Throws(IOException::class)
fun newWriter(file: File): BufferedWriter {
    return BufferedWriter(FileWriter(file))
}

fun fileStartsWith(file: File, vararg strings: String): Boolean {
    var flag = false
    var i = 0
    while (i < strings.size && !flag) {
        flag = file.name.startsWith(strings[i])
        i++
    }
    return flag
}