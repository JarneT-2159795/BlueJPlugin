package org.bluejplugin.actions;

import bluej.extensions2.BClass;
import bluej.extensions2.editor.TextLocation;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.LanguageRegistry;
import org.bluejplugin.Actions;
import org.bluejplugin.BlueJManager;
import org.bluejplugin.Comment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * In this class we use PMD to check the code for other errors
 *
 * @author Jarne Thys
 */
public class PmdAction extends Action
{
    private final BClass bClass;
    private final String projectDir;

    /**
     * Constructor for objects of class PmdAction
     *
     * @param code      Source code of the class
     * @param maxPoints Maximum points for this action
     * @param bClass    The class that is being evaluated
     */
    public PmdAction(String code, int maxPoints, BClass bClass)
    {
        super(code, maxPoints);
        this.bClass = bClass;
        try
        {
            this.projectDir = BlueJManager.getInstance().getBlueJ().getCurrentPackage().getDir().getAbsolutePath();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void performAction(Actions actions)
    {
        PMDConfiguration config = new PMDConfiguration();
        config.setDefaultLanguageVersion(LanguageRegistry.PMD.getLanguageVersionById("java", "20"));
        config.prependAuxClasspath("target/classes");
        config.setMinimumPriority(RulePriority.LOW);

        Path path = Paths.get(BlueJManager.getInstance().getBlueJ().getUserConfigDir().toString(), "pmd-ruleset.xml");
        if (!Files.exists(path))
        {
            try
            {
                Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/pmd-ruleset.xml")), path);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        config.addRuleSet(path.toString());
        config.setReportFormat("json");
        File reportFile = new File(projectDir + "/pmd-report.json");
        config.setReportFile(reportFile.toPath());
        int errors = 0;

        try (PmdAnalysis pmd = PmdAnalysis.create(config))
        {
            pmd.files().addFile(bClass.getJavaFile().toPath());
            pmd.performAnalysis();

            Reader reader = Files.newBufferedReader(reportFile.toPath());
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(reader);
            var files = (JSONArray) json.get("files");
            if (!files.isEmpty())
            {
                var file = (JSONObject) files.get(0);
                var violations = (JSONArray) file.get("violations");

                for (int i = 0; i < violations.size(); i++)
                {
                    var violation = (JSONObject) violations.get(i);
                    String message = violation.get("description").toString() + ", (line " + violation.get("beginline") + ")";
                    actions.addComment(new Comment(message,
                            new TextLocation(Integer.parseInt(violation.get("beginline").toString()) - 1, 0),
                            new URL(violation.get("externalInfoUrl").toString())));
                    errors++;
                }
            }

            points = maxPoints - (errors / 2);
            if (points < 0)
            {
                points = 0;
            }

            reportFile.delete();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
