package net.tetrakoopa.mdu4j.test.service.dataquery;

import com.google.gson.Gson;

import net.tetrakoopa.mdu4j.service.dataquery.RIQueryService;

import org.testng.annotations.Test;

import java.io.IOException;

public class RIQueryServiceTest {

	Gson GSON = new Gson();

	RIQueryService queryService = new RIQueryService() {

	};


	@Test
	public void checkOperators() throws IOException {
		// String operatorsStr =
		// IOUtil.readString("/net/tetrakoopa/mdu4j/service/dataquery/operators.json");

		//List<Operator> operators = queryService.createOperators();
		

	}

}
