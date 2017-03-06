package net.tetrakoopa.mdu4j.admin.front.servlet.bean.log;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonRequestParameter;

public interface LogRequestParameter extends CommonRequestParameter {

    String KEY_LOG_APPENDER_NAME = "appender-name";

    String KEY_LOG_MODE = "mode";

    String KEY_LOG_INDEX = "index";

    String KEY_LOG_DATE = "date";
}
