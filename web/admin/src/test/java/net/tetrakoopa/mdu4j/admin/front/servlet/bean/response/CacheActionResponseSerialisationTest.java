package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheActionResponse;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheResponse;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;
import net.tetrakoopa.mdu4j.util.test.AbstractSerialisationTest;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CacheActionResponseSerialisationTest extends AbstractSerialisationTest {

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
    @Ignore
    public void testCacheResetJson() {

        String text =  serializeJson(buildResetResponse());
        String expected = loadJsonResourceForThisMethod();

        assertEquals(expected, text);
    }

    @Test
    @Ignore
    public void testCacheResetXml() {

        String text =  serializeXml(buildResetResponse());
        String expected = loadXmlResourceForThisMethod();

        assertEquals(expected, text);

    }

    @Test
    @Ignore
    public void testCacheViewJson() {

        String text =  serializeJson(buildViewResponse());
        String expected = loadJsonResourceForThisMethod();

        assertEquals(expected, text);

    }

    @Test
    @Ignore
    public void testCacheViewXml() {

        String text =  serializeXml(buildViewResponse());
        String expected = loadXmlResourceForThisMethod();

        assertEquals(expected, text);

    }

    private CacheActionResponse buildViewResponse() {

        final CacheActionResponse response = new CacheActionResponse();

        ActionResponse.Status status = new ActionResponse.Status();
        status.setCodeAndMessage(ActionResponse.Status.Code.SUCCESS,"All is goooood");

        response.setStatus(status);
        response.setAction(CacheAction.VIEW);

        response.setInvolvedCaches(new ArrayList<CacheResponse>());

        CacheResponse cache = new CacheResponse();
        cache.setName("Cachon");
        cache.getContent().put("Truc",new TrucDeCache(5,"lalala").toString());
        response.getInvolvedCaches().add(cache);

        cache = new CacheResponse();
        cache.setName("Cachou-le-cache-casse-cou");
        cache.getContent().put("Clavier","azerty");
        cache.getContent().put("Nouriture","fraise des bois");
        cache.getContent().put("Nouriture2","chocolat");
        response.getInvolvedCaches().add(cache);

        return response;
    }

    private CacheActionResponse buildResetResponse() {
        final CacheActionResponse response = new CacheActionResponse();

        ActionResponse.Status status = new ActionResponse.Status();
        status.setCodeAndMessage(ActionResponse.Status.Code.SUCCESS,"All is goooood");

        response.setStatus(status);
        response.setAction(CacheAction.RESET);

        response.setInvolvedCaches(new ArrayList<CacheResponse>());

        CacheResponse cache = new CacheResponse();
        cache.setName("Cachix");
        response.getInvolvedCaches().add(cache);

        cache = new CacheResponse();
        cache.setName("Cacache-le-cache");
        response.getInvolvedCaches().add(cache);

        return response;
    }
}
