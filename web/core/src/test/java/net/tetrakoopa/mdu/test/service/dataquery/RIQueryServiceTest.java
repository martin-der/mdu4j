package net.tetrakoopa.mdu.test.service.dataquery;

import java.io.IOException;
import java.util.List;

import net.tetrakoopa.mdu.service.dataquery.RIQueryService;
import net.tetrakoopa.mdu.service.dataquery.model.Operator;
import net.tetrakoopa.mdu.service.dataquery.model.DataEngine;
import net.tetrakoopa.mdu4j.util.IOUtil;

import org.junit.Test;

import com.google.gson.Gson;

public class RIQueryServiceTest {

	Gson GSON = new Gson();

	RIQueryService queryService = new RIQueryService() {

	};

	@Test
	public void ttt() throws IOException {      
		String templateStr = IOUtil.readString("net/tetrakoopa/mdu/service/dataquery/query-dbtemplate-response.json");

		GSON.fromJson(templateStr, DataEngine.class);
		

	}

	@Test
	public void checkOperators() throws IOException {
		// String operatorsStr =
		// IOUtil.readString("/net/tetrakoopa/mdu/service/dataquery/operators.json");

		List<Operator> operators = queryService.createOperators();
		

	}

}
