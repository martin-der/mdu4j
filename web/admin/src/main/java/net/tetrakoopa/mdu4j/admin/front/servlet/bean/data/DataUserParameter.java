package net.tetrakoopa.mdu4j.admin.front.servlet.bean.data;


import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.DynamicForm;

public class DataUserParameter extends CommonUserParameter {

    @Attribute.Key(DataRequestParameter.KEY_DATA_TYPE)
    @DynamicForm.Attribute.Label("Type")
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Order(1)
    private String dataType;

    @Attribute.Key(DataRequestParameter.KEY_DATA_ID)
    @DynamicForm.Attribute.Label("Id")
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Order(0)
    private String dataId;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

}
