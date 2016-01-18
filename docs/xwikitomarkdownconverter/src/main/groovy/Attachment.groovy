import java.nio.file.Path

/**
 * Created by kalinowski on 07.10.15.
 */
class Attachment {

    def public StringBuilder attachmentContent

    def public String fileName

    def public Attachment() {
        attachmentContent = new StringBuilder()
    }

    void writeToPath(Path path) {
        path = path.resolve(fileName)
        println "writing attachment to ${path}"
        if (path.toFile().exists()) {
            System.err.println("Warning: file exists: " + path);
        }
        def decoder = new Base64.Decoder(false, false)
        def bytes = decoder.decode(attachmentContent.toString())

        path.parent.toFile().mkdirs()
        def os = path.newOutputStream()
        os.write(bytes)
        os.close()
    }
}
