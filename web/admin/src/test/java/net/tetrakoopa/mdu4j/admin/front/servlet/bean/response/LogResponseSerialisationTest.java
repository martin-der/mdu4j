package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.admin.front.servlet.bean.log.LogAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.LogActionResponse;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.LogResponse;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;
import net.tetrakoopa.mdu4j.logger.bean.Logger;
import net.tetrakoopa.mdu4j.util.test.AbstractSerialisationTest;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LogResponseSerialisationTest extends AbstractSerialisationTest {

    public static class TrucDeCache {

        private int taille;

        private String tendance;

        public TrucDeCache(int taille, String tendance) {
            this.taille = taille;
            this.tendance = tendance;
        }

        public int getTaille() {
            return taille;
        }

        public void setTaille(int taille) {
            this.taille = taille;
        }

        public String getTendance() {
            return tendance;
        }

        public void setTendance(String tendance) {
            this.tendance = tendance;
        }

        @Override
        public String toString() {
            return "taille="+taille+"|tendance="+tendance;
        }

    }


    @Test
    public void testLogListJson() {

        String text =  serializeJson(buildListResponse());
        String expected = loadJsonResourceForThisMethod();

        assertEquals(expected, text);
    }

    @Test
    public void testLogListXml() {

        String text =  serializeXml(buildListResponse());
        String expected = loadXmlResourceForThisMethod();

        assertEquals(expected, text);

    }

    @Test
    public void testLogViewJson() {

        String text =  serializeJson(buildViewResponse());
        String expected = loadJsonResourceForThisMethod();

        assertEquals(expected, text);

    }

    @Test
    public void testCacheViewXml() {

        String text =  serializeXml(buildViewResponse());
        String expected = loadXmlResourceForThisMethod();

        assertEquals(expected, text);

    }

    private LogActionResponse buildListResponse() {

        final LogActionResponse response = new LogActionResponse();

        ActionResponse.Status status = new ActionResponse.Status();
        status.setCodeAndMessage(ActionResponse.Status.Code.SUCCESS,"All is goooood");

        response.setStatus(status);
        response.setAction(LogAction.VIEW);

        response.setInvolvedLogs(new ArrayList<LogResponse>());

        Logger logger = new Logger();
        logger.setName("Regular");
        logger.setFile(new File("c:/some/where/log"));
        response.getInvolvedLogs().add(LogResponse.Factory.createFromLogger(logger));


        logger = new Logger();
        logger.setName("SecurityWatchFile");
        File fileParentLog = new File("c:/some/where/else/log");
        logger.setFile(fileParentLog);
        response.getInvolvedLogs().add(LogResponse.Factory.createFromLogger(logger));

        logger = new Logger();
        logger.setName("SecurityWatchFile");
        logger.setFile(new File("c:/some/where/else/log.2"));
        response.getInvolvedLogs().add(LogResponse.Factory.createFromRolledFileLogger(logger, fileParentLog));

        return response;
    }

    private LogActionResponse buildViewResponse() {

        final LogActionResponse response = new LogActionResponse();

        ActionResponse.Status status = new ActionResponse.Status();
        status.setCodeAndMessage(ActionResponse.Status.Code.SUCCESS,"All is goooood");

        response.setStatus(status);
        response.setAction(LogAction.VIEW);

        response.setInvolvedLogs(new ArrayList<LogResponse>());

        LogResponse log = new LogResponse();
        log.setContent("qdqsdqs");
        response.getInvolvedLogs().add(log);


        return response;
    }

}
