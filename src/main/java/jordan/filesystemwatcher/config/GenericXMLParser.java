package jordan.filesystemwatcher.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

class GenericXMLParser<XMLBeanType> {

    XMLBeanType parsedObject = null;
    Class<XMLBeanType> xmlBeanTypeClass;

    XMLBeanType getParsedObject() {
        return parsedObject;
    }

    public GenericXMLParser(Class<XMLBeanType> clazz) {
        xmlBeanTypeClass = clazz;
    }

    public GenericXMLParser(String filename, Class<XMLBeanType> clazz) {
        xmlBeanTypeClass = clazz;
        parse(filename);
    }

    public GenericXMLParser(File file, Class<XMLBeanType> clazz) {
        xmlBeanTypeClass = clazz;
        parse(file);
    }

    public GenericXMLParser parse(String filename) {
        return parse(new File(filename));
    }

    public GenericXMLParser parse(File file) {
        return parseInternal(file);
    }

    protected GenericXMLParser parseInternal(File file) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(xmlBeanTypeClass);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();

            parsedObject = (XMLBeanType) unmarshaller.unmarshal(file);

        } catch (Exception e) {
            System.err.println("GenericXMLParser.parseInternal: exception occurred! " + e.getMessage());
        }
        return this;
    }
}