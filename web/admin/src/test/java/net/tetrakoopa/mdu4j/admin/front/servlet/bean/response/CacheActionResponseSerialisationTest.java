package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheActionResponse;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheResponse;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;
import net.tetrakoopa.mdu4j.util.test.AbstractSerialisationTest;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public void testCacheResetJson() {

        String text =  serializeJson(buildResetResponse());
        String expected = loadJsonResourceForThisMethod();

        assertEquals(expected, text);
    }

    @Test
    public void testCacheResetXml() {

        String text =  serializeXml(buildResetResponse());
        String expected = loadXmlResourceForThisMethod();

        assertEquals(expected, text);

    }

    @Test
    public void testCacheViewJson() {

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

    private CacheActionResponse buildViewResponse() {

        final CacheActionResponse response = new CacheActionResponse();

        ActionResponse.Status status = new ActionResponse.Status();
        status.setCodeAndMessage(ActionResponse.Status.Code.SUCCESS,"All is goooood");

        response.setStatus(status);
        response.setAction(CacheAction.VIEW);

        response.setInvolvedCaches(new ArrayList<CacheResponse>());

        CacheResponse cache = createCacheResponse();
        cache.setName("Cachon");
        cache.getContent().put("Truc",new TrucDeCache(5,"lalala").toString());
        response.getInvolvedCaches().add(cache);

        cache = createCacheResponse();
        cache.setName("Cachou-le-cache-casse-cou");
        cache.getContent().put("Clavier","azerty");
        cache.getContent().put("Nouriture","fraise des bois");
        cache.getContent().put("Nouriture2","chocolat");
        response.getInvolvedCaches().add(cache);

        return response;
    }

    public static class OrderedContentCacheResponse extends CacheResponse {

    	@Override
    	public Map<String, Object> getContent() {
			final Field contentField = ReflectionUtils.findField(this.getClass(), "content");
			ReflectionUtils.makeAccessible(contentField);
			if (ReflectionUtils.getField(contentField, this)==null)
				ReflectionUtils.setField(contentField, this, new LinkedHashMap());
			return super.getContent();
		}

	}

    /**
     * Create a 'CacheResponse' with 'LinkedHashMap' for content.
     * This way cache content will be ordered
     */
    private CacheResponse createCacheResponse() {
        return new OrderedContentCacheResponse();
    }

    private CacheActionResponse buildResetResponse() {
        final CacheActionResponse response = new CacheActionResponse();

        ActionResponse.Status status = new ActionResponse.Status();
        status.setCodeAndMessage(ActionResponse.Status.Code.SUCCESS,"All is goooood");

        response.setStatus(status);
        response.setAction(CacheAction.RESET);

        response.setInvolvedCaches(new ArrayList<CacheResponse>());

        CacheResponse cache = createCacheResponse();
        cache.setName("Cachix");
        response.getInvolvedCaches().add(cache);

        cache = createCacheResponse();
        cache.setName("Cacache-le-cache");
        response.getInvolvedCaches().add(cache);

        return response;
    }
}
