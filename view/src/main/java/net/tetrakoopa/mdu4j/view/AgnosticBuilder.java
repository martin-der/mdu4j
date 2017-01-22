package net.tetrakoopa.mdu4j.util.view;


/**
 * Classe parente des classes servant de monteur pour une vue<br/>
 */
public abstract class AgnosticBuilder {
	
	/** Type de donnée => influe sur le Contrôle généré */
	public static enum InputType {
		/** No content : display a label */
		SINGLE_LABEL,
		/** display a input field */
		TEXT,
		/** display a input field for number */
		NUMBER,
		/** display a yes/no choice */
		BOOLEAN,
		/**
		 * display a choice to select one element amongst many elements<br/>
		 * typical render is a <i>Drop Down List</i> or a <i>List with one selectable element at once</i>
		 */
		PICK_ONE,
		/**
		 * display a choice to select many elements amongst many elements<br/>
		 * typical render is a <i>Drop Down List allowing multiple selection</i>
		 */
		PICK_MANY
	}
	
	/** Type Spécific view choice */
	public static class ElementPresentation<W> {
		/** How to display a boolean input */
		@SuppressWarnings("javadoc")
		public static class Boolean<W>
		extends ElementPresentation<W> {
			public static enum Widget {
				CHECK_BOX,
				DROP_DOWN;
			}
			public final W widget;
			
			public Boolean(final W widget) {
				this.widget = widget;
			}
		}
		
		/** How to display a OneAmongstMany input */
		@SuppressWarnings("javadoc")
		public static enum OneAmongstMany {
			RADIO_BUTTONS,
			DROP_DOWN;
		}
	}
	
}
