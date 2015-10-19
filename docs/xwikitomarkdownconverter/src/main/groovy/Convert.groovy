import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile

class Convert {
    def private SAXParser parser
    def private XWikiConverter xwikiConverter

    public static void main(String[] args) {
        def c = new Convert()
        c.unzipAll(new File("xwiki").toPath())
    }

    def public Convert() {
        def instance = SAXParserFactory.newInstance()
        instance.namespaceAware = false
        parser = instance.newSAXParser();

        xwikiConverter=new XWikiConverter()


    }

    def public unzipAll(Path p) {
        p.eachFile {Path f ->
            if (f.toFile().isFile() && f.fileName.toFile().name.endsWith(".xar") ) {
//                println "found zipfile: ${f.fileName}"
                try {
                    def zip = new ZipFile(f.toFile())
                } catch (ZipException e ) {
                    println "Error opening: $f"
                    e.printStrackTrace()
                }
                def List<ZipEntry> found = zip.entries().findAll {entry ->
                    entry.name.endsWith(".xml") && !entry.name.endsWith("package.xml") //&& entry.name.contains("01 Introduction")
                }

                found.each {it ->
                    println "parsing ${it.name}"
                    convertXwikiToMarkdown(zip.getInputStream(it), it.name)
                }
            }
        }
    }

    def public convertXwikiToMarkdown(InputStream is, String fileName) {

        def StringBuilder stringContent
        def Attachment attachment
        def String attachmentFilename;

        def outPath = new File(fileName).toPath()
        def targetDir = Paths.get("build/markdown", outPath.parent.toFile().name)
        targetDir.toFile().mkdirs()
        def outFile = targetDir.resolve(outPath.subpath(1, outPath.nameCount))
        def newName = outFile.fileName.toString().replace(".xml", ".md")

        parser.parse(is, new DefaultHandler() {
            def boolean contentParsing = false;
            def boolean attachmentParsing = false;
            def boolean filenameParsing = false;

            @Override
            void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes)
                if (qName.equals("content")) {
                    contentParsing =true
//                    println "startContent"
                    stringContent = new StringBuilder()
                } else if (qName.equals("attachment")) {
                    attachmentParsing = true;
//                    println "startAttachment"
                    attachment = new Attachment();
                } else if (qName.equals("filename")) {
                    filenameParsing = true;
                    attachmentFilename = new String()
                }

            }

            @Override
            void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName)
                if (qName.equals("content")) {
                    contentParsing = false
//                    println "stopContent"
                    if (attachmentParsing) {

                    } else {
                        def convertedString = convertWikiStringToMarkdownString(stringContent.toString())
                        convertedString = postProcessMarkdown(convertedString, mdReplacements)
                        def mdOutputFile = outFile.resolveSibling(newName);
                        println "writing converted markdown to ${mdOutputFile}"
                        mdOutputFile.write(convertedString)
                    }
                } else if (qName.equals("attachment")) {
                    attachmentParsing = false
                    attachment.writeToFile(targetDir)
//                    println "stopAttachment"
                } else if (qName.equals("filename")) {
                    filenameParsing = false;
                    attachment.fileName = attachmentFilename
                }
            }

            @Override
            void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length)
                if (attachmentParsing)  {
                    if (filenameParsing) {
                        attachmentFilename += new String(ch, start, length)
                    } else if (contentParsing) {
                        attachment.attachmentContent.append new String(ch, start, length)
                    }
                } else {
                    if (contentParsing) {
                        if (length > 1) {
                            stringContent.append new String(ch, start, length)
                        }
                    }
                }



            }
        })



//        return result
    }

    def int macroCounter
    def Queue<String[]> macroContent

    String convertWikiStringToMarkdownString(String s) {
        macroCounter = 0;
        macroContent = new LinkedBlockingQueue<String[]>()

//        s = applyReplacements(s, preprocessReplacements);
        s = preprocessXwiki(s)

        def htmlString = xwikiConverter.convert(s)
        def builder = new ProcessBuilder('/usr/bin/pandoc', '-f', 'html', '-t', 'markdown', '--no-wrap');
        builder.redirectErrorStream(true)
        def p = builder.start();

        def bytes = new ByteArrayOutputStream()
        def readerThread = new StreamGobbler(p.inputStream, "", bytes);
        readerThread.start()

        def outWriter = p.out.newWriter()
        outWriter.write(htmlString)
        outWriter.flush()
        p.out.close()

        readerThread.join()

        return bytes.toString()
//        return htmlString
    }

    String preprocessXwiki(String s) {

        def matcher = preprocessReplacements.codeMacro.pattern.matcher(s);
        def sb = new StringBuffer()
        while (matcher.find()) {
            def String lang = null
            def String content = null
            if (matcher.groupCount() == 2) {
                content = matcher.group(2)
                lang = matcher.group(1)
            } else {
                content = matcher.group(1)
            }
            macroContent.add([lang, content] as String[])
            def String replacement = "XXXCODEBLOCKXXX"
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            macroCounter++;
        }
        matcher.appendTail(sb)
        return applyReplacements(sb.toString(), preprocessReplacements)
    }

    String postProcessMarkdown(String s, replacements) {
        def pattern = ~/XXXCODEBLOCKXXX/
        def matcher = pattern.matcher(s)

        def sb = new StringBuffer()
        while (matcher.find()) {
            def codeBlock = macroContent.remove()
            def String lang = codeBlock[0]? codeBlock[0] : ""
            def String replacement = "\n```${lang}\n${codeBlock[1]}\n```"
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            macroCounter++;
        }
        matcher.appendTail(sb)

        return applyReplacements(sb.toString(), replacements)
    }


    def preprocessReplacements = [
            codeMacro: [ // {{code language="java"}} .... {{/code}}
                    pattern: ~/\{\{code(?: language="(\w*)")?\}\}([\W\w]*?)\{\{\\/code\}\}/,
                    replacement: 'XXXBACKTICKSXXX$1\n$2\nXXXBACKTICKSXXX',
            ],
            macroWorkaround: [
                pattern: ~/\{\{(\w*) ([^\}]*)\}\}([\w\W]*)\{\{\\/\1\}\}/,
                replacement: 'BEGIN MACRO: $1 param: $2 \n $3\n END MACRO: $1'
            ],
            singleTagMacroWorkaround: [ // {{toc start="2" depth="2"/}}
                    pattern: ~/\{\{(\w*) ([^\}]*)\\/\}\}/,
                    replacement: 'BEGIN MACRO: $1 param: $2 END MACRO: $1'
            ]
    ]

    def mdReplacements = [
            img: [
                    pattern: ~/!\[([^\]]*@\w*.\w*)\]\(([^\]]*)@(\w*.\w*)\)/,
                    replacement: '!\\[$1\\]\\($3\\)'
                    ],
            headlinks: [
                    pattern: ~/\{#.*\}/,
                    replacement: ''
                    ],
            newlines: [
                    pattern: Pattern.compile("\\\\"),
                    replacement: '\n'
            ],
            linksRelative: [
                    pattern: ~/([^!])\[([^\]]*)\]\((?:doc:){0}([^h][^t][^t][^p][\S]*)\.([\S]*)\)/,
                    replacement: '$1[$2]($3/$4)'
            ],
            linksAbsolute: [
                    pattern: ~/([^!])\[([^\]]*)\]\((?:doc:)([^h][^t][^t][^p][\S]*)\.([\S]*)\)/,
                    replacement: '$1[$2](/$3/$4)'
            ],
            linkWithoutSubdirRel: [
                    pattern: ~/([^!])\[([^\]]*)\]\((?:doc:){0}([^h][^t][^t][^p][^)]*)\)/,
                    replacement: '$1[$2]($3)'
            ],
            linkWithoutSubdirAbs: [
                    pattern: ~/([^!])\[([^\]]*)\]\((?:doc:)([^h][^t][^t][^p][^)]*)\)/,
                    replacement: '$1[$2](/$3)'
            ],
//            backticks: [
//                    pattern: ~/XXXBACKTICKSXXX/,
//                    replacement: '```'
//            ]


    ]


    String applyReplacements(String s, replacements) {

        replacements.each {rep ->
            def matcher = rep.value.pattern.matcher(s)
            s = matcher.replaceAll(rep.value.replacement)
        }

        return s;
    }
}

