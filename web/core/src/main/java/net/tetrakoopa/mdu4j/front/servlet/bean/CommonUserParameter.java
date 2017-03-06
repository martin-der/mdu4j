package net.tetrakoopa.mdu4j.front.servlet.bean;

public class CommonUserParameter implements CommonRequestParameter {

    @DynamicForm.Attribute.Excluded
    @Attribute.Key({"content-type", KEY_CONTENT_TYPE})
    private ContentType reponseContentType;

    public ContentType getReponseContentType() {
        return reponseContentType;
    }

    public void setReponseContentType(ContentType reponseContentType) {
        this.reponseContentType = reponseContentType;
    }
    
}
