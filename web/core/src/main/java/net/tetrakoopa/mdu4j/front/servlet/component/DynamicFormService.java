package net.tetrakoopa.mdu4j.front.servlet.component;

import net.tetrakoopa.mdu4j.front.servlet.bean.DynamicForm;
import net.tetrakoopa.mdu4j.front.servlet.bean.ParameterDynamicForm;
import net.tetrakoopa.mdu4j.util.StringUtil;

import org.springframework.util.ReflectionUtils;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;

public abstract  class DynamicFormService<B> implements DynamicForm.Attribute {

    private static class EntryComparator implements Comparator<String> {

        private final Map<String, DynamicForm.FormEntry> entries;

        public EntryComparator(Map<String, DynamicForm.FormEntry> entries) {
            this.entries = entries;
        }

        @Override
        public int compare(String key1, String key2) {
            final DynamicForm.FormEntry entry1 = entries.get(key1);
            final DynamicForm.FormEntry entry2 = entries.get(key2);
            final int order1 = entry1.order;
            final int order2 = entry2.order;
            if (order1 < order2)
                return -1;
            if (order1 > order2)
                return 1;
            return 0;
        }

    }

    public void renderDynamicForm(PrintWriter writer, String formAcion, DynamicForm form, String submitName) {
        writer.println("<form enctype='application/x-www-form-urlencoded' method='get' action='"+formAcion+"' >");
        renderDynamicInputsInTable(writer, form);
        writer.println("<br/>");
        writer.println("<input type='submit' value='"+ StringUtil.escapeXml(submitName)+"'>");
        writer.println("<form>");
    }
    public void renderDynamicInputsInTable(PrintWriter writer, DynamicForm form) {

        final List<String> entryKeys = new ArrayList<String>(form.entries.keySet());
        Collections.sort(entryKeys, new EntryComparator(form.entries));

        writer.println("<table class='dynamic-form'>");
        for (String key : entryKeys) {
            final DynamicForm.FormEntry formEntry = form.entries.get(key);
            if (!formEntry.hidden) {
                writer.print("<tr>");
                writer.print("<td>");
                writer.print(StringUtil.escapeHtml(formEntry.label));
                writer.print("</td>");
                writer.print("<td>");
                writer.print("<input name='" + formEntry.name + "' ");
                writer.print("type='");
                writer.print("text");
                writer.print("' ");
                writer.print("value='");
                writer.print(StringUtil.escapeXml(formEntry.value));
                writer.print("' ");
                if (formEntry.mandatory) {
                    writer.print(ParameterDynamicForm.UI_ATTRIBUTE_PREFIX+":mandatory='true' ");
                }
                writer.print(ParameterDynamicForm.UI_ATTRIBUTE_PREFIX+":type='" + formEntry.type.name() + "' ");
                for (Map.Entry<String, String> attributes : formEntry.attributes().entrySet()) {
                    writer.print(attributes.getKey());
                    writer.print("='");
                    writer.print(StringUtil.escapeXml(attributes.getValue()));
                    writer.print("'");
                }
                writer.print("/>");
                writer.print("</td>");
                writer.println("</tr>");
            }
        }
        writer.println("</table>");
        for (Map.Entry<String, DynamicForm.FormEntry> entry : form.entries.entrySet()) {
            final DynamicForm.FormEntry formEntry = entry.getValue();
            if (formEntry.hidden) {
                writer.print("<input name='" + formEntry.name + "' ");
                writer.print("type='hidden' ");
                writer.print("value='");
                writer.print(StringUtil.escapeXml(formEntry.value));
                writer.print("' ");
                writer.println("/>");
            }
        }

    }

    public void populateForm(final DynamicForm form, final B parameter) {
        populateForm(form, parameter, null);
    }
    public void populateForm(final DynamicForm form, final B parameter, final Map<String, Map<String, String>> extraAttributes) {
        ReflectionUtils.doWithFields(parameter.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                boolean hidden = false;
                final Excluded excludedAnnotation = field.getAnnotation(Excluded.class);
                if (excludedAnnotation !=null) {
                    hidden = excludedAnnotation.hidden();
                    if (!hidden) {
                        return;
                    }
                }
                final DynamicForm.FormEntry entry = buildFormEntry(field, parameter, hidden);
                if (extraAttributes!=null) {
                    final Map<String, String> extra = extraAttributes.get(field.getName());
                    if (extra != null) {
                        entry.addAttributes(extra);
                    }

                }
                form.entries.put(field.getName(), entry);
            }
        });
    }

    private DynamicForm.FormEntry buildFormEntry(Field field,B parameter, boolean hidden) {
        ReflectionUtils.makeAccessible(field);
        final Object rawValue = ReflectionUtils.getField(field, parameter);
        final Order orderAnnotation = field.getAnnotation(Order.class);
        final Label labelAnnotation = field.getAnnotation(Label.class);
        final int order =  orderAnnotation == null ? Integer.MAX_VALUE : orderAnnotation.value();
        final DynamicForm.FormEntry.Type type = DynamicForm.FormEntry.Type.fromClass(field.getType());

        final String label = labelAnnotation == null ? getName(field) : labelAnnotation.value();

        final String value;
        if (rawValue == null) {
            value = "";
        } else {
            final String stringValue = getValue(rawValue);
            value = stringValue != null ?  stringValue : rawValue.toString();
        }


        return new DynamicForm.FormEntry(getName(field), label, value, type,false, hidden, order);
    }

    protected abstract String getValue(Object value);

    protected String getName(final Field field) {
        return field.getName();
    }

}
