package net.tetrakoopa.mdu4j.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Data {

	enum FieldMandatory {
		NEVER, ALWAYS
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Mandatory {
		FieldMandatory value() default FieldMandatory.ALWAYS;
	}

	interface Validation {
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@interface Regex {
			String value();
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@interface Inode {
			enum Type {
				FOLDER, FILE, FOLDER_FILE
			}
			boolean mustExist() default true;
			boolean canBeSymlink() default true;
			Type type() default Type.FILE;
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@interface Min {
			long value();
		}
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@interface Max {
			long value();
		}
	}

}
