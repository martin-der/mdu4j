package net.tetrakoopa.mdu4j.front.servlet.bean;


public enum ContentType implements HtmlParameterEnum, CommonRequestParameter {

    @Attribute.Default
    HTML,
    JSON,
    XML;
    
    
    private final String parameterName;
    
    ContentType() {
        parameterName = this.name().toLowerCase();
    }

    public static ContentType fromParameterName(String name) {
        if (name == null) {
            return null;
        }
        
        for (ContentType contentType: values() ) {
            if (contentType.getParameterName().equals(name)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("No such "+ContentType.class.getName()+" with parameterName '"+name+"'");
    }

    @Override
    public String getParameterName() {
        return parameterName; 
    }
}
