package net.tetrakoopa.mdu4j.front.servlet.bean;


public interface HtmlParameterEnum {

    String getParameterName();

    class NoSuchEnumException extends Exception {
        public NoSuchEnumException() {

        }
        public NoSuchEnumException(String message) {
            super(message);
        }
    }

    class Util {
        public static <E extends Enum<E>> E[] getActions(Class<E> enumClass) {
            checkType(enumClass);

            return  enumClass.getEnumConstants();
        }
        public static <E extends Enum<E>> E fromParameterName(Class<E> enumClass, String name) throws NoSuchEnumException {

            checkType(enumClass);

            if (name == null) {
                return null;
            }

            for (E enuum : enumClass.getEnumConstants() ) {
                if (((HtmlParameterEnum)enuum).getParameterName().equals(name)) {
                    return enuum;
                }
            }

            throw new NoSuchEnumException("No such "+enumClass.getName()+" with parameter name '"+name+"'");
        }

        public static <E extends Enum<E>> String[] buildActionsParameterName(Class<E> enumClass) {

            checkType(enumClass);

            final E enums[] = enumClass.getEnumConstants();

            final String names[] = new String[enums.length];
            int index = 0;
            for (E enuum:enums ) {
                names[index++] = ((HtmlParameterEnum)enuum).getParameterName();
            }
            return names;
        }
        public static <E extends Enum<E>> HtmlParameterEnum asHtmlParameterEnum  (E enuum) {
            checkType(enuum.getClass());
            return (HtmlParameterEnum)enuum;
        }

        private static <E extends Enum<E>> void checkType(Class<E> enumClass) {
            if (!HtmlParameterEnum.class.isAssignableFrom(enumClass)) {
                throw new IllegalArgumentException("Enum must implements "+HtmlParameterEnum.class.getName());
            }
        }
    }
}
