package scripting;

import client.MapleClient;
import server.config.ServerEnvironment;
import tools.StringUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Matze
 */
public abstract class AbstractScriptManager {

    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbstractScriptManager.class);
    private static boolean isDebugMode;
    private final ScriptEngineManager sem;
    protected ScriptEngine engine;

    protected AbstractScriptManager() {
        isDebugMode = ServerEnvironment.isDebugEnabled();
        sem = new ScriptEngineManager();
    }

    protected Invocable getInvocable(String path, String scriptId, MapleClient c) {
        try {
            path = ServerEnvironment.getConfig().getScriptsPath() + "/" + path + "/" + scriptId + ".js";

            if (isDebugMode) {
                log.info("Loading file " + path);
            }

            engine = sem.getEngineByName("nashorn");
            if (c != null) {
                c.setScriptEngine(path, engine);
            }
            StringBuilder builder = new StringBuilder();
            builder.append("load('nashorn:mozilla_compat.js');" + System.lineSeparator());
            builder.append("function scriptName(){ return \"$1\"; }".replace("$1", scriptId));
            String scriptsPath = ServerEnvironment.getConfig().getScriptsPath();
            builder.append("function getScriptPath(){ return \"" + scriptsPath + "\" }");
            String content = StringUtil.readFileAsString(path);
            if (content.isEmpty()) {
                return null;
            } else {
                builder.append(content);
            }

            engine.eval(builder.toString());
            return (Invocable) engine;
        } catch (ScriptException e) {
            log.error("Error executing script " + path, e);
            log.info("Error executing script " + path + e.getMessage() + " line: " + e.getLineNumber()
                    + " column: " + e.getColumnNumber());
            return null;
        } catch (Exception ex) {
            log.error("Error executing script " + path, ex);
            log.info("Error executing script " + path);
            return null;
        }

    }

    protected void resetContext(String path, MapleClient c) {
        path = ServerEnvironment.getConfig().getScriptsPath() + "/" + path;
        c.removeScriptEngine(path);
    }

}