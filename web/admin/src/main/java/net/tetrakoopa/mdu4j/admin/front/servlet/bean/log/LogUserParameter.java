package net.tetrakoopa.mdu4j.admin.front.servlet.bean.log;



import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.DynamicForm;

import java.util.Date;

public class LogUserParameter extends CommonUserParameter {

    @Attribute.Key(LogRequestParameter.KEY_LOG_MODE)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("Mode")
    @DynamicForm.Attribute.Order(10)
    private RetrievalMode mode;

    @Attribute.Key(LogRequestParameter.KEY_LOG_APPENDER_NAME)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("Name")
    @DynamicForm.Attribute.Order(0)
    private String appenderName;

    @Attribute.Key(LogRequestParameter.KEY_LOG_DATE)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("Date")
    @DynamicForm.Attribute.Order(2)
    private Date date;

    @Attribute.Key(LogRequestParameter.KEY_LOG_INDEX)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("Index")
    @DynamicForm.Attribute.Order(0)
    private Integer index;

    public String getAppenderName() {
        return appenderName;
    }

    public void setAppenderName(String appenderName) {
        this.appenderName = appenderName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public RetrievalMode getMode() {
        return mode;
    }

    public void setMode(RetrievalMode mode) {
        this.mode = mode;
    }

}
