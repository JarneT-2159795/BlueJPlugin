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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        config.addRuleSet("rulesets/java/quickstart.xml");
        config.setReportFormat("text");
        File reportFile = new File(projectDir + "\\pmd-report.txt");
        config.setReportFile(reportFile.toPath());

        try (PmdAnalysis pmd = PmdAnalysis.create(config))
        {
            pmd.files().addFile(bClass.getJavaFile().toPath());
            pmd.performAnalysis();

            String content = new String(Files.readAllBytes(Paths.get(reportFile.getAbsolutePath())));
            String[] lines = content.split("\\r?\\n");
            for (String line : lines)
            {
                if (line.contains("NoPackage"))
                {
                    // BlueJ does not create a package for the class, so we ignore this error
                    continue;
                }
                // C:\Users\Jarne Thys\Desktop\Test\NogTest.java:16:	UnnecessaryConstructor:	Avoid unnecessary constructors - the compiler will generate these for you
                String[] parts = line.split("java:");
                int lineNr = Integer.parseInt(parts[1].split(":")[0]);
                String message = parts[1].split(lineNr + ":\t")[1];
                actions.addComment(new Comment(message, new TextLocation(lineNr - 1, 0)));
            }

            reportFile.delete();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
