package net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties;


import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.DynamicForm;

public class PropertiesUserParameter extends CommonUserParameter {

    @Attribute.Key(PropertiesRequestParameter.KEY_PROPERTIES_RESOURCE_CLASSPATH)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("Resource")
    @DynamicForm.Attribute.Order(10)
    private String resourcePath;

    @Attribute.Key(PropertiesRequestParameter.KEY_PROPERTIES_RESOURCE_FILE_SYSTEM)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("File")
    @DynamicForm.Attribute.Order(10)
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

}
