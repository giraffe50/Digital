package de.neemann.digital.gui.components;

import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.element.*;
import de.neemann.digital.lang.Lang;
import de.neemann.gui.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Simple Dialog to show an elements help text.
 * <p>
 * Created by hneemann on 25.10.16.
 */
public class ElementHelpDialog extends JDialog {

    private static final int MAX_WIDTH = 600;
    private static final int MAX_HEIGHT = 800;

    /**
     * Creates a new instance
     *
     * @param parent            the parents dialog
     * @param elementType       the type of the element
     * @param elementAttributes the attributes of this element
     */
    public ElementHelpDialog(JDialog parent, ElementTypeDescription elementType, ElementAttributes elementAttributes) {
        super(parent, Lang.get("attr_help"), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final String description = getDetailedDescription(elementType, elementAttributes);
        JEditorPane editorPane = new JEditorPane("text/html", description);
        editorPane.setEditable(false);
        getContentPane().add(new JScrollPane(editorPane));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(new JButton(new AbstractAction(Lang.get("ok")) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        }));
        getContentPane().add(buttons, BorderLayout.SOUTH);

        pack();
        Dimension r = getSize();
        if (r.width > MAX_WIDTH) r.width = MAX_WIDTH;
        if (r.height > MAX_HEIGHT) r.height = MAX_HEIGHT;
        setSize(r);
        setLocationRelativeTo(parent);
    }

    /**
     * Creates a detailed human readable description of this element
     *
     * @param et                the element to describe
     * @param elementAttributes the actual attributes of the element to describe
     * @return the human readable description of this element
     */
    private static String getDetailedDescription(ElementTypeDescription et, ElementAttributes elementAttributes) {
        StringBuilder sb = new StringBuilder("<html><body>");
        addHTMLDescription(sb, et, elementAttributes);
        return sb.append("</body></html>").toString();
    }

    /**
     * Adds the description of the given element to the given StringBuilder.
     *
     * @param sb                the StringBuilder to use
     * @param et                the element to describe
     * @param elementAttributes the actual attributes of the element to describe
     */
    public static void addHTMLDescription(StringBuilder sb, ElementTypeDescription et, ElementAttributes elementAttributes) {
        sb.append("<h3>").append(et.getTranslatedName()).append("</h3>\n");
        String descr = et.getDescription(elementAttributes);
        if (!descr.equals(et.getTranslatedName()))
            sb.append("<p>").append(StringUtils.breakLines(et.getDescription(elementAttributes))).append("</p>\n");

        try {
            PinDescriptions inputs = et.getInputDescription(elementAttributes);
            sb.append("<h4>").append(Lang.get("elem_Help_inputs")).append(":</h4>\n<dl>\n");
            if (inputs != null && inputs.size() > 0) {
                for (PinDescription i : inputs)
                    sb.append("<dt><i>").append(i.getName()).append("</i></dt><dd>").append(i.getDescription()).append("</dd>\n");
            }
            sb.append("</dl>\n");
        } catch (NodeException e) {
            e.printStackTrace();
        }

        PinDescriptions outputs = et.getOutputDescriptions(elementAttributes);
        sb.append("<h4>").append(Lang.get("elem_Help_outputs")).append(":</h4>\n<dl>\n");
        if (outputs != null && outputs.size() > 0) {
            for (PinDescription i : outputs)
                sb.append("<dt><i>").append(i.getName()).append("</i></dt><dd>").append(i.getDescription()).append("</dd>\n");
        }
        sb.append("</dl>\n");

        if (et.getAttributeList().size() > 0) {
            sb.append("<h4>").append(Lang.get("elem_Help_attributes")).append(":</h4>\n<dl>\n");
            for (Key k : et.getAttributeList())
                sb.append("<dt><i>").append(k.getName()).append("</i></dt><dd>").append(k.getDescription()).append("</dd>\n");
            sb.append("</dl>\n");
        }
    }
}