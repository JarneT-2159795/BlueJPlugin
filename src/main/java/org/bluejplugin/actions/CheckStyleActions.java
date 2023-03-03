package org.bluejplugin.actions;

import bluej.extensions2.PackageNotFoundException;
import bluej.extensions2.ProjectNotOpenException;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.bluejplugin.Actions;
import org.bluejplugin.Comment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class keeps a collection of CSACtion objects.
 *
 * @author Leo Rutten
 * @version 30/ 3/2015
 */
public class CheckStyleActions extends Action
{
    // The key is CODE, not CSCODE
    private final HashMap<String, CSAction> csactions;
    private Actions actions;

    /**
     * Constructs a <code>CheckStyleActions</code>.
     */
    public CheckStyleActions(String cde, int maxp)
    {
        super(cde, maxp);
        System.out.println("CheckStyleActions()");
        csactions = new HashMap<String, CSAction>();
    }

    public void add(CSAction action)
    {
        csactions.put(action.getCode(), action);
    }

    /**
     * Search the action on the action CODE.
     */
    @Override
    public Action search(String code)
    {
        return csactions.get(code);
    }

    /**
     * Search the action on the CheckStyle CODE.
     *
     * @param cscde CSCODE
     */
    public CSAction cssearch(String cscde)
    {
        for (String k : csactions.keySet())
        {
            CSAction csa = csactions.get(k);
            CSAction csa2 = csa.cssearch(cscde);
            if (csa2 != null)
            {
                return csa2;
            }
        }
        return null;
    }

    @Override
    public boolean isInapplicable()
    {
        inapplicable = true;

        for (String k : csactions.keySet())
        {
            CSAction csa = csactions.get(k);
            if (!csa.isInapplicable())
            {
                inapplicable = false;
            }
        }

        return inapplicable;
    }

    private void logPoints()
    {
        System.out.println("---- logpoints ----");
        for (String k : csactions.keySet())
        {
            CSAction csa = csactions.get(k);
            if (!csa.isInapplicable())
            {
                int p = csa.getPoints();
                int mp = csa.getMaxPoints();
                System.out.println("csaction " + csa.getCode() + " " + p + "/" + mp);
            } else
            {
                System.out.println("csaction " + csa.getCode() + " inapplicable");
            }
        }
        System.out.println("---- ---- ----");
    }

    @Override
    public int getPoints()
    {
        logPoints();
        int p = 0;
        boolean inapp = true;
        for (String k : csactions.keySet())
        {
            CSAction csa = csactions.get(k);
            if (!csa.isInapplicable())
            {
                inapp = false;
                p += csa.getPoints();
            }
        }
        if (inapp)
        {
            inapplicable = true;
        }
        return p;
    }

    @Override
    public int getMaxPoints()
    {
        int mp = 0;
        boolean inapp = true;
        for (String k : csactions.keySet())
        {
            CSAction csa = csactions.get(k);
            if (!csa.isInapplicable())
            {
                mp += csactions.get(k).getMaxPoints();
            }
        }
        if (inapp)
        {
            inapplicable = true;
        }
        return mp;
    }

    @Override
    public void performAction(Actions actns)
    {
        this.actions = actns;
        final Checker c = new Checker();
        c.setModuleClassLoader(CheckStyleActions.class.getClassLoader());
        try
        {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("default_checks.xml");
            Configuration config = ConfigurationLoader.loadConfiguration(inputStream,
                    new PropertiesExpander(new Properties()), true);
            c.configure(config);

            final AuditListener auditor = new Auditor();
            c.addListener(auditor);

            ArrayList<File> files = new ArrayList<File>();
            try
            {
                files.add(actions.getBClass().getJavaFile());

                // lock TreeWalker for call to static method parse
                synchronized (TreeWalker.class)
                {
                    int n = c.process(files);
                    System.out.println("# processed " + n);
                }
            } catch (ProjectNotOpenException | PackageNotFoundException | CheckstyleException e)
            {
                System.out.println("CheckstyleException " + e);

                c.destroy();
            }
        } catch (CheckstyleException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void handleError(AuditEvent aEvt)
    {
        System.out.println("handleError ");
        int column = aEvt.getColumn();
        int line = aEvt.getLine();
        String message = aEvt.getMessage();
        String cskey = aEvt.getLocalizedMessage().getKey();


        //CSAction act = csactions.get(key);
        CSAction act = cssearch(cskey);
        if (act != null)
        {
            System.out.println("action found");
            if (act.isActive())
            {
                Comment comment = act.handleError(line, column, message);
                if (comment != null)
                {
                    System.out.println("comment received");
                    actions.addComment(comment);
                }
            } else
            {
                System.out.println("action not active");
            }
        } else
        {
            System.out.println("action not found");
        }
    }

    class Auditor implements AuditListener
    {
        /**
         * @see com.puppycrawl.tools.checkstyle.api.AuditListener
         */
        public void auditStarted(AuditEvent aEvt)
        {
            System.out.println("auditStarted() " + aEvt);
        }

        /**
         * @see com.puppycrawl.tools.checkstyle.api.AuditListener
         */
        public void auditFinished(AuditEvent aEvt)
        {
            System.out.println("auditFinished() " + aEvt);
        }

        /**
         * @see com.puppycrawl.tools.checkstyle.api.AuditListener
         */
        public void fileStarted(AuditEvent aEvt)
        {
            System.out.println("fileStarted() " + aEvt);
        }

        /**
         * @see com.puppycrawl.tools.checkstyle.api.AuditListener
         */
        public void fileFinished(AuditEvent aEvt)
        {
            System.out.println("fileFinished() " + aEvt);
        }

        /**
         * @see com.puppycrawl.tools.checkstyle.api.AuditListener
         */
        public void addError(AuditEvent aEvt)
        {
            //System.out.println("addError() " + aEvt);
            //System.out.println("   column " + aEvt.getColumn());
            //System.out.println("   line   " + aEvt.getLine());
            System.out.println("   locmessage " + aEvt.getLocalizedMessage());
            System.out.println("      key " + aEvt.getLocalizedMessage().getKey());
            //System.out.println("   message " + aEvt.getMessage());

            handleError(aEvt);
        }

        /**
         * @see com.puppycrawl.tools.checkstyle.api.AuditListener
         */
        public void addException(AuditEvent aEvt, Throwable aThrowable)
        {
            System.out.println("addException() " + aEvt);
        }
    }
}                    

