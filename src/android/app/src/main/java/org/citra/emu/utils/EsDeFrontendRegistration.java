package org.citra.emu.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import org.citra.emu.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class EsDeFrontendRegistration {
    public static final String ACTION_REGISTER_ES_DE_FRONTEND = "register_es_de_frontend";
    public static final String ACTION_PICK_ES_DE_CUSTOM_SYSTEMS_FOLDER =
        "pick_es_de_custom_systems_folder";
    public static final String KEY_ES_DE_CUSTOM_SYSTEMS_PATH =
        "es_de_custom_systems_path";

    private static final String PREF_ES_DE_CUSTOM_SYSTEMS_PATH = "es_de_custom_systems_path";
    private static final String PREF_ES_DE_CUSTOM_SYSTEMS_URI = "es_de_custom_systems_uri";
    private static final String ES_FIND_RULES_FILE = "es_find_rules.xml";
    private static final String ES_SYSTEMS_FILE = "es_systems.xml";
    private static final String ES_DE_DIR = "ES-DE";
    private static final String ES_DE_CUSTOM_SYSTEMS_DIR = "custom_systems";
    private static final String ES_DE_SETTINGS_DIR = "settings";
    private static final String ES_DE_LOGS_DIR = "logs";
    private static final String ES_DE_GAMELISTS_DIR = "gamelists";
    private static final String ES_DE_SETTINGS_FILE = "es_settings.xml";
    private static final String ES_DE_LOG_FILE = "es_log.txt";
    private static final String BJJ_LABEL = "Citra BJJ (Standalone)";
    private static final String BJJ_FIND_RULE = "org.citra.bjj/org.citra.emu.ui.EmulationActivity";
    private static final String BJJ_COMMAND = "%EMULATOR_CITRA-BJJ% %EXTRA_GamePath%=%ROM%";
    private static final String N3DS_EXTENSION =
        ".3ds .3DS .3dsx .3DSX .app .APP .axf .AXF .cci .CCI .cxi .CXI .elf .ELF .z3dsx " +
        ".Z3DSX .zcci .ZCCI .zcxi .ZCXI .7z .7Z .zip .ZIP";
    private static final List<CommandEntry> N3DS_COMMANDS = Arrays.asList(
        new CommandEntry("Azahar (Standalone)",
                         "%EMULATOR_AZAHAR% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% " +
                         "%DATA%=%ROMSAF%"),
        new CommandEntry("AzaharPlus (Standalone)",
                         "%EMULATOR_AZAHARPLUS% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% " +
                         "%DATA%=%ROMSAF%"),
        new CommandEntry("Citra",
                         "%EMULATOR_RETROARCH% " +
                         "%EXTRA_CONFIGFILE%=%EXTERNALDATA%/Android/data/%ANDROIDPACKAGE%/files/retroarch.cfg " +
                         "%EXTRA_LIBRETRO%=%INTERNALDATA%/%ANDROIDPACKAGE%/cores/citra_libretro_android.so " +
                         "%EXTRA_ROM%=%ROM%"),
        new CommandEntry("Citra (Standalone)",
                         "%EMULATOR_CITRA% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% " +
                         "%DATA%=%ROMSAF%"),
        new CommandEntry("Citra Canary (Standalone)",
                         "%EMULATOR_CITRA-CANARY% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% " +
                         "%DATA%=%ROMSAF%"),
        new CommandEntry("Citra MMJ (Standalone)",
                         "%EMULATOR_CITRA-MMJ% %EXTRA_GamePath%=%ROM%"),
        new CommandEntry(BJJ_LABEL, BJJ_COMMAND),
        new CommandEntry("Mandarine (Standalone)",
                         "%EMULATOR_MANDARINE% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% " +
                         "%DATA%=%ROMSAF%"),
        new CommandEntry("Lime3DS (Standalone)",
                         "%EMULATOR_LIME3DS% %ACTIVITY_CLEAR_TASK% %ACTIVITY_CLEAR_TOP% " +
                         "%DATA%=%ROMSAF%"),
        new CommandEntry("Panda3DS (Standalone)",
                         "%EMULATOR_PANDA3DS% %DATA%=%ROMPROVIDER%"));

    private EsDeFrontendRegistration() {}

    public static String getSavedCustomSystemsPath(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String path = preferences.getString(PREF_ES_DE_CUSTOM_SYSTEMS_PATH, "");
        if (path == null || path.isEmpty()) {
            return null;
        }
        return path;
    }

    public static Uri getSavedCustomSystemsUri(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uri = preferences.getString(PREF_ES_DE_CUSTOM_SYSTEMS_URI, "");
        if (uri == null || uri.isEmpty()) {
            return null;
        }
        return Uri.parse(uri);
    }

    public static void saveCustomSystemsPath(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_ES_DE_CUSTOM_SYSTEMS_PATH, path)
            .remove(PREF_ES_DE_CUSTOM_SYSTEMS_URI)
            .apply();
    }

    public static void saveCustomSystemsUri(Context context, Uri uri) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .remove(PREF_ES_DE_CUSTOM_SYSTEMS_PATH)
            .putString(PREF_ES_DE_CUSTOM_SYSTEMS_URI, uri.toString())
            .apply();
    }

    public static void clearSavedCustomSystemsPath(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .remove(PREF_ES_DE_CUSTOM_SYSTEMS_PATH)
            .apply();
    }

    public static void clearSavedCustomSystemsUri(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .remove(PREF_ES_DE_CUSTOM_SYSTEMS_URI)
            .apply();
    }

    public static String getFolderSummary(Context context) {
        String path = getSavedCustomSystemsPath(context);
        if (path != null) {
            return context.getString(R.string.setting_es_de_custom_systems_folder_summary, path);
        }

        Uri uri = getSavedCustomSystemsUri(context);
        if (uri == null) {
            if (canRegisterUsingDefaultPath(context)) {
                return getDefaultFolderSummary();
            }
            return "";
        }

        String label = getTreeDisplayName(context, uri);
        return context.getString(R.string.setting_es_de_custom_systems_folder_summary, label);
    }

    public static String getFolderValue(Context context) {
        String path = getSavedCustomSystemsPath(context);
        if (path != null) {
            return path;
        }

        if (canRegisterUsingDefaultPath(context)) {
            return getDefaultFolderSummary();
        }

        Uri uri = getSavedCustomSystemsUri(context);
        if (uri == null) {
            return "";
        }

        return getTreeDisplayName(context, uri);
    }

    public static String getRegistrationSummary(Context context) {
        String path = getSavedCustomSystemsPath(context);
        if (path != null) {
            return context.getString(R.string.setting_es_de_register_ready_description, path);
        }

        if (canRegisterUsingDefaultPath(context)) {
            return context.getString(R.string.setting_es_de_register_default_description,
                                     getDefaultFolderSummary());
        }

        Uri uri = getSavedCustomSystemsUri(context);
        if (uri == null) {
            return "";
        }

        String label = getTreeDisplayName(context, uri);
        return context.getString(R.string.setting_es_de_register_ready_description, label);
    }

    public static void register(Context context, Uri treeUri) throws Exception {
        DocumentFile customSystemsDir = resolveCustomSystemsDirectory(context, treeUri);
        upsertFindRules(context, customSystemsDir);
        upsertSystems(context, customSystemsDir);
    }

    public static void registerUsingSelectedPath(Context context, String path) throws Exception {
        File customSystemsDir = resolveSelectedCustomSystemsDirectory(new File(path));
        upsertFindRules(customSystemsDir);
        upsertSystems(customSystemsDir);
    }

    public static boolean canRegisterUsingDefaultPath(Context context) {
        return PermissionsHandler.hasWriteAccess(context);
    }

    public static void registerUsingDefaultPath(Context context) throws Exception {
        File customSystemsDir = resolveDefaultCustomSystemsDirectory();
        upsertFindRules(customSystemsDir);
        upsertSystems(customSystemsDir);
    }

    public static String getDefaultFolderSummary() {
        return new File(resolveDefaultEsDeHomeDirectory(), ES_DE_CUSTOM_SYSTEMS_DIR).getAbsolutePath();
    }

    private static DocumentFile resolveCustomSystemsDirectory(Context context, Uri treeUri) {
        DocumentFile root = DocumentFile.fromTreeUri(context, treeUri);
        if (root == null || !root.isDirectory() || !root.canWrite()) {
            throw new IllegalArgumentException("Selected ES-DE folder is not writable");
        }

        String name = root.getName();
        if (ES_DE_CUSTOM_SYSTEMS_DIR.equals(name)) {
            return root;
        }

        if (ES_DE_DIR.equals(name)) {
            DocumentFile esDeHome = resolveSelectedEsDeHome(root);
            DocumentFile customSystemsDir = esDeHome.findFile(ES_DE_CUSTOM_SYSTEMS_DIR);
            if (customSystemsDir == null) {
                customSystemsDir = esDeHome.createDirectory(ES_DE_CUSTOM_SYSTEMS_DIR);
            }
            if (customSystemsDir != null && customSystemsDir.isDirectory() &&
                customSystemsDir.canWrite()) {
                return customSystemsDir;
            }
            throw new IllegalArgumentException(
                "Selected ES-DE folder does not provide a writable custom_systems directory");
        }

        throw new IllegalArgumentException(
            "Selected folder is not ES-DE or ES-DE/custom_systems");
    }

    private static File resolveDefaultCustomSystemsDirectory() throws IOException {
        File esDeHome = resolveDefaultEsDeHomeDirectory();
        if (!esDeHome.exists() && !esDeHome.mkdirs()) {
            throw new IOException("Could not create ES-DE home directory");
        }

        File customSystemsDir = new File(esDeHome, ES_DE_CUSTOM_SYSTEMS_DIR);
        if (!customSystemsDir.exists() && !customSystemsDir.mkdirs()) {
            throw new IOException("Could not create custom_systems directory");
        }

        if (!customSystemsDir.isDirectory() || !customSystemsDir.canWrite()) {
            throw new IOException("Default custom_systems directory is not writable");
        }

        return customSystemsDir;
    }

    private static File resolveSelectedCustomSystemsDirectory(File selected) throws IOException {
        if (!selected.exists() || !selected.isDirectory()) {
            throw new IllegalArgumentException("Selected ES-DE folder is not a directory");
        }

        String name = selected.getName();
        if (ES_DE_CUSTOM_SYSTEMS_DIR.equals(name)) {
            if (!selected.canWrite()) {
                throw new IOException("Selected custom_systems directory is not writable");
            }
            return selected;
        }

        if (ES_DE_DIR.equals(name)) {
            File esDeHome = resolveSelectedEsDeHome(selected);
            File customSystemsDir = new File(esDeHome, ES_DE_CUSTOM_SYSTEMS_DIR);
            if (!customSystemsDir.exists() && !customSystemsDir.mkdirs()) {
                throw new IOException("Could not create custom_systems directory");
            }
            if (!customSystemsDir.isDirectory() || !customSystemsDir.canWrite()) {
                throw new IOException(
                    "Selected ES-DE folder does not provide a writable custom_systems directory");
            }
            return customSystemsDir;
        }

        throw new IllegalArgumentException(
            "Selected folder is not ES-DE or ES-DE/custom_systems");
    }

    private static File resolveDefaultEsDeHomeDirectory() {
        File storageRoot = Environment.getExternalStorageDirectory();
        File outerEsDeDir = new File(storageRoot, ES_DE_DIR);
        File nestedEsDeDir = new File(outerEsDeDir, ES_DE_DIR);
        File[] candidates = new File[] { nestedEsDeDir, outerEsDeDir };

        File bestCandidate = outerEsDeDir;
        long bestScore = Long.MIN_VALUE;
        for (File candidate : candidates) {
            long score = scoreEsDeHomeDirectory(candidate);
            if (score > bestScore) {
                bestCandidate = candidate;
                bestScore = score;
            }
        }

        return bestCandidate;
    }

    private static File resolveSelectedEsDeHome(File root) {
        File nested = new File(root, ES_DE_DIR);
        if (!nested.isDirectory()) {
            return root;
        }

        long rootScore = scoreEsDeHomeDirectory(root);
        long nestedScore = scoreEsDeHomeDirectory(nested);
        return nestedScore > rootScore ? nested : root;
    }

    private static long scoreEsDeHomeDirectory(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return Long.MIN_VALUE;
        }

        long score = 0;
        File settingsFile = new File(new File(directory, ES_DE_SETTINGS_DIR), ES_DE_SETTINGS_FILE);
        if (settingsFile.isFile()) {
            score += 1_000_000_000_000L;
        }

        File gamelistsDir = new File(directory, ES_DE_GAMELISTS_DIR);
        if (gamelistsDir.isDirectory()) {
            score += 100_000_000_000L;
        }

        File customSystemsDir = new File(directory, ES_DE_CUSTOM_SYSTEMS_DIR);
        if (customSystemsDir.isDirectory()) {
            score += 10_000_000_000L;
        }

        File logFile = new File(new File(directory, ES_DE_LOGS_DIR), ES_DE_LOG_FILE);
        if (logFile.isFile()) {
            score += logFile.lastModified();
        }

        return score;
    }

    private static DocumentFile resolveSelectedEsDeHome(DocumentFile root) {
        DocumentFile nested = root.findFile(ES_DE_DIR);
        if (nested == null || !nested.isDirectory()) {
            return root;
        }

        long rootScore = scoreEsDeHomeDirectory(root);
        long nestedScore = scoreEsDeHomeDirectory(nested);
        return nestedScore > rootScore ? nested : root;
    }

    private static long scoreEsDeHomeDirectory(DocumentFile directory) {
        if (directory == null || !directory.isDirectory()) {
            return Long.MIN_VALUE;
        }

        long score = 0;
        DocumentFile settingsDir = directory.findFile(ES_DE_SETTINGS_DIR);
        if (settingsDir != null && settingsDir.isDirectory() &&
            settingsDir.findFile(ES_DE_SETTINGS_FILE) != null) {
            score += 1_000_000_000_000L;
        }

        DocumentFile gamelistsDir = directory.findFile(ES_DE_GAMELISTS_DIR);
        if (gamelistsDir != null && gamelistsDir.isDirectory()) {
            score += 100_000_000_000L;
        }

        DocumentFile customSystemsDir = directory.findFile(ES_DE_CUSTOM_SYSTEMS_DIR);
        if (customSystemsDir != null && customSystemsDir.isDirectory()) {
            score += 10_000_000_000L;
        }

        DocumentFile logsDir = directory.findFile(ES_DE_LOGS_DIR);
        if (logsDir != null && logsDir.isDirectory()) {
            DocumentFile logFile = logsDir.findFile(ES_DE_LOG_FILE);
            if (logFile != null) {
                score += logFile.lastModified();
            }
        }

        return score;
    }

    private static void upsertFindRules(Context context, DocumentFile root) throws Exception {
        Document document = loadOrCreateDocument(context, root, ES_FIND_RULES_FILE, "ruleList");
        Element rootElement = document.getDocumentElement();
        Element emulator = findElementByAttribute(rootElement, "emulator", "name", "CITRA-BJJ");
        if (emulator == null) {
            emulator = document.createElement("emulator");
            emulator.setAttribute("name", "CITRA-BJJ");
            rootElement.appendChild(emulator);
        }

        removeAllChildren(emulator);

        Element rule = document.createElement("rule");
        rule.setAttribute("type", "androidpackage");
        emulator.appendChild(rule);

        Element entry = document.createElement("entry");
        entry.setTextContent(BJJ_FIND_RULE);
        rule.appendChild(entry);

        writeDocument(context, root, ES_FIND_RULES_FILE, document);
    }

    private static void upsertFindRules(File root) throws Exception {
        Document document = loadOrCreateDocument(root, ES_FIND_RULES_FILE, "ruleList");
        Element rootElement = document.getDocumentElement();
        Element emulator = findElementByAttribute(rootElement, "emulator", "name", "CITRA-BJJ");
        if (emulator == null) {
            emulator = document.createElement("emulator");
            emulator.setAttribute("name", "CITRA-BJJ");
            rootElement.appendChild(emulator);
        }

        removeAllChildren(emulator);

        Element rule = document.createElement("rule");
        rule.setAttribute("type", "androidpackage");
        emulator.appendChild(rule);

        Element entry = document.createElement("entry");
        entry.setTextContent(BJJ_FIND_RULE);
        rule.appendChild(entry);

        writeDocument(root, ES_FIND_RULES_FILE, document);
    }

    private static void upsertSystems(Context context, DocumentFile root) throws Exception {
        Document document = loadOrCreateDocument(context, root, ES_SYSTEMS_FILE, "systemList");
        Element rootElement = document.getDocumentElement();
        Element system = findSystem(rootElement, "n3ds");
        if (system == null) {
            system = createN3dsSystem(document);
            rootElement.appendChild(system);
        } else {
            upsertCommand(document, system, BJJ_LABEL, BJJ_COMMAND, "Citra MMJ (Standalone)");
        }

        writeDocument(context, root, ES_SYSTEMS_FILE, document);
    }

    private static void upsertSystems(File root) throws Exception {
        Document document = loadOrCreateDocument(root, ES_SYSTEMS_FILE, "systemList");
        Element rootElement = document.getDocumentElement();
        Element system = findSystem(rootElement, "n3ds");
        if (system == null) {
            system = createN3dsSystem(document);
            rootElement.appendChild(system);
        } else {
            upsertCommand(document, system, BJJ_LABEL, BJJ_COMMAND, "Citra MMJ (Standalone)");
        }

        writeDocument(root, ES_SYSTEMS_FILE, document);
    }

    private static Element createN3dsSystem(Document document) {
        Element system = document.createElement("system");
        appendTextElement(document, system, "name", "n3ds");
        appendTextElement(document, system, "fullname", "Nintendo 3DS");
        appendTextElement(document, system, "path", "%ROMPATH%/n3ds");
        appendTextElement(document, system, "extension", N3DS_EXTENSION);
        for (CommandEntry command : N3DS_COMMANDS) {
            appendCommand(document, system, command.label, command.value);
        }
        appendTextElement(document, system, "platform", "n3ds");
        appendTextElement(document, system, "theme", "n3ds");
        return system;
    }

    private static void upsertCommand(Document document, Element system, String label, String value,
                                      String afterLabel) {
        Element existing = findCommand(system, label);
        if (existing != null) {
            existing.setTextContent(value);
            return;
        }

        Element command = document.createElement("command");
        command.setAttribute("label", label);
        command.setTextContent(value);

        Element after = findCommand(system, afterLabel);
        if (after != null) {
            Node insertAfter = after.getNextSibling();
            if (insertAfter != null) {
                system.insertBefore(command, insertAfter);
            } else {
                system.appendChild(command);
            }
            return;
        }

        Element platform = findDirectChild(system, "platform");
        if (platform != null) {
            system.insertBefore(command, platform);
        } else {
            system.appendChild(command);
        }
    }

    private static void appendCommand(Document document, Element system, String label,
                                      String value) {
        Element command = document.createElement("command");
        command.setAttribute("label", label);
        command.setTextContent(value);
        system.appendChild(command);
    }

    private static void appendTextElement(Document document, Element parent, String tag,
                                          String value) {
        Element child = document.createElement(tag);
        child.setTextContent(value);
        parent.appendChild(child);
    }

    private static Document loadOrCreateDocument(Context context, DocumentFile root, String fileName,
                                                 String rootName) throws Exception {
        DocumentFile file = root.findFile(fileName);
        if (file == null || file.length() == 0) {
            return createDocument(rootName);
        }

        ContentResolver resolver = context.getContentResolver();
        try (InputStream inputStream = resolver.openInputStream(file.getUri())) {
            if (inputStream == null) {
                throw new IOException("Could not open " + fileName);
            }
            Document document = newDocumentBuilder().parse(inputStream);
            Element rootElement = document.getDocumentElement();
            if (rootElement == null || !rootName.equals(rootElement.getTagName())) {
                throw new IOException("Unexpected root element in " + fileName);
            }
            return document;
        }
    }

    private static Document loadOrCreateDocument(File root, String fileName, String rootName)
        throws Exception {
        File file = new File(root, fileName);
        if (!file.exists() || file.length() == 0) {
            return createDocument(rootName);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            Document document = newDocumentBuilder().parse(inputStream);
            Element rootElement = document.getDocumentElement();
            if (rootElement == null || !rootName.equals(rootElement.getTagName())) {
                throw new IOException("Unexpected root element in " + fileName);
            }
            return document;
        }
    }

    private static void writeDocument(Context context, DocumentFile root, String fileName,
                                      Document document) throws Exception {
        DocumentFile file = root.findFile(fileName);
        if (file == null) {
            file = root.createFile("application/xml", fileName);
        }
        if (file == null) {
            throw new IOException("Could not create " + fileName);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        try (OutputStream outputStream =
                 context.getContentResolver().openOutputStream(file.getUri(), "wt")) {
            if (outputStream == null) {
                throw new IOException("Could not open output stream for " + fileName);
            }
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        }
    }

    private static void writeDocument(File root, String fileName, Document document)
        throws Exception {
        File file = new File(root, fileName);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        try (OutputStream outputStream = new FileOutputStream(file, false)) {
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        }
    }

    private static Document createDocument(String rootName) throws ParserConfigurationException {
        Document document = newDocumentBuilder().newDocument();
        Element root = document.createElement(rootName);
        document.appendChild(root);
        return document;
    }

    private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        trySetFeature(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newDocumentBuilder();
    }

    private static void trySetFeature(DocumentBuilderFactory factory, String name, boolean value)
        throws ParserConfigurationException {
        try {
            factory.setFeature(name, value);
        } catch (ParserConfigurationException e) {
            Log.w("citra", "XML parser does not support feature " + name + ", continuing without it",
                  e);
        }
    }

    private static void removeAllChildren(Element element) {
        while (element.hasChildNodes()) {
            element.removeChild(element.getFirstChild());
        }
    }

    private static Element findSystem(Element root, String systemName) {
        NodeList systems = root.getElementsByTagName("system");
        for (int i = 0; i < systems.getLength(); i++) {
            Node node = systems.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;
                Element name = findDirectChild(element, "name");
                if (name != null && systemName.equals(name.getTextContent())) {
                    return element;
                }
            }
        }
        return null;
    }

    private static Element findCommand(Element system, String label) {
        NodeList commands = system.getElementsByTagName("command");
        for (int i = 0; i < commands.getLength(); i++) {
            Node node = commands.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;
                if (label.equals(element.getAttribute("label"))) {
                    return element;
                }
            }
        }
        return null;
    }

    private static Element findElementByAttribute(Element root, String tagName,
                                                  String attributeName, String attributeValue) {
        NodeList elements = root.getElementsByTagName(tagName);
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;
                if (attributeValue.equals(element.getAttribute(attributeName))) {
                    return element;
                }
            }
        }
        return null;
    }

    private static Element findDirectChild(Element parent, String tagName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element && tagName.equals(node.getNodeName())) {
                return (Element)node;
            }
        }
        return null;
    }

    private static String getTreeDisplayName(Context context, Uri uri) {
        DocumentFile directory = DocumentFile.fromTreeUri(context, uri);
        if (directory != null && directory.getName() != null) {
            return directory.getName();
        }
        return uri.toString();
    }

    private static final class CommandEntry {
        private final String label;
        private final String value;

        private CommandEntry(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
}
