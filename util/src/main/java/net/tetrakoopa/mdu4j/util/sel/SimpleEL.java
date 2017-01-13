package net.tetrakoopa.mdu4j.util.sel;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.tetrakoopa.mdu4j.util.ReflectionUtil;
import net.tetrakoopa.mdu4j.util.sel.annotation.SimpleELAttribut;
import net.tetrakoopa.mdu4j.util.sel.exception.ELNullPointerException;
import net.tetrakoopa.mdu4j.util.sel.exception.ELSyntaxException;
import net.tetrakoopa.mdu4j.util.sel.exception.SimpleELException;
import net.tetrakoopa.mdu4j.util.sel.exception.UnknownAttributException;
import net.tetrakoopa.mdu4j.util.sel.exception.UnknownObjectException;

public class SimpleEL {
	
	public final static String DEFAULT_NULL_OBJECT = "<null>";
	
	public static Object evaluateExpression(Map<String, Object> objects, String expression) throws SimpleELException {

		String tokens[] = expression.split("\\.");

		if (tokens.length == 0)
			throw new UnknownObjectException("Expected object name");

		final String token = tokens[0].trim();
		if (token.isEmpty())
			throw new ELSyntaxException("Expected object name", 0);

		if (!objects.containsKey(token))
			throw new UnknownObjectException("Unkown object '" + token + "'");

		final Object object = objects.get(token);

		if (tokens.length == 1)
			return object;

		String popedTokens[] = Arrays.copyOfRange(tokens, 1, tokens.length);

		return getAttributeFromTokenizedExpression(objects, object, popedTokens);

	}

	public static String evaluateExpressionAndConvertToString(SimpleELContext context, String expression) throws SimpleELException {
		return evaluateExpressionAndConvertToString(context, expression, null);
	}

	public static String evaluateExpressionAndConvertToString(SimpleELContext context, String expression, String nullStringValue) throws SimpleELException {

		final Object rawResult = evaluateExpression(context.getObjects(), expression);

		return rawResult == null ? (nullStringValue == null ? DEFAULT_NULL_OBJECT : nullStringValue) : context.convertToView(rawResult);
	}

	public static Object evaluateExpression(Object object, String objectName, String expression) throws SimpleELException {
		Map<String, Object> objects = new HashMap<String, Object>();
		objects.put(objectName, object);
		return evaluateExpression(objects, expression);
	}


	private static Object getAttributeFromTokenizedExpression(Map<String, Object> objects, Object object, String tokens[]) throws ELSyntaxException,
			UnknownAttributException,
			ELNullPointerException {
		int i = 0;

		for (String token : tokens ) {

			token = token.trim();

			if (token.isEmpty())
				throw new ELSyntaxException("Expected attribute name", 0);

			if (i++ >= tokens.length)
				break;

			if (object == null)
				throw new ELNullPointerException("Attribut '" + token + "' resolved to null");

			final Field field;
			try {
				field = ReflectionUtil.getField(object.getClass(), token);
			} catch (RuntimeException rex) {
				throw new UnknownAttributException(token);
			}

			SimpleELAttribut simpleELAttribut = field.getAnnotation(SimpleELAttribut.class);

			if (simpleELAttribut != null) {
				if (simpleELAttribut.ignore()) {
					throw new UnknownAttributException(token);
				}
				final String name = simpleELAttribut.name().trim();
				if (!name.isEmpty())
					token = name;
			}

			try {
				object = ReflectionUtil.get(object, token);
			} catch (RuntimeException rex) {
				throw new UnknownAttributException(token);
			}

		}
		return object;
	}

}
