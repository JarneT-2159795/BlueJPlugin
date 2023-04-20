package org.bluejplugin.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import org.bluejplugin.Actions;
import org.bluejplugin.BlueJManager;
import org.bluejplugin.actions.Action;
import org.bluejplugin.actions.GetterCSAction;

/**
 * <p>
 * Checks for the existence of getters.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="GetterCheck"/&gt;
 * </pre>
 * <p>
 * The companion action class is GetterCSAction
 *
 * @author Leo Rutten
 * @version 1.0
 */
public class GetterCheck extends Check
{
    /**
     * Maximum children allowed *
     */
    private static final int MAX_CHILDREN = 7;

    /**
     * Maximum children allowed *
     */
    private static final int BODY_SIZE = 3;

    private static final String CODE = GetterCSAction.CODE;
    private static final String CSCODE = GetterCSAction.CSCODE;

    private static final String ANNOTATIONCLASS = "Intentional";
    private static final String NOGETTER = "noGetter";

    @Override
    public int[] getDefaultTokens()
    {
        return new int[]{
                TokenTypes.CLASS_DEF
        };
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        System.out.println("GetterCheck.visitToken()");
        System.out.println("   aAST " + aAST);

        // loop through all variable definitions
        DetailAST objblock = aAST.getLastChild();
        DetailAST child = objblock.getFirstChild();
        int varctr = 0;
        while (child != null)
        {
            System.out.println("      child " + child);

            if (child.getType() == TokenTypes.VARIABLE_DEF)
            {
                System.out.println("      variabledef");
                varctr++;

                // get the name of the variable
                String varname = "";
                DetailAST ident = child.findFirstToken(TokenTypes.IDENT);
                if (ident != null)
                {
                    varname = ident.getText();
                }
                System.out.println("         varname " + varname);

                // Check annotation
                boolean noGetter = false;
                DetailAST modifiers = child.findFirstToken(TokenTypes.MODIFIERS);
                if (modifiers != null)
                {
                    DetailAST annotation = modifiers.findFirstToken(TokenTypes.ANNOTATION);
                    System.out.println("         annotation " + annotation);
                    if (annotation != null)
                    {
                        DetailAST annoIdent = annotation.findFirstToken(TokenTypes.IDENT);
                        System.out.println("         annotation ident " + annoIdent);
                        if (annoIdent.getText().equals(ANNOTATIONCLASS))
                        {
                            System.out.println("         is Intentional");

                            DetailAST annoch = annotation.getFirstChild();
                            while (annoch != null)
                            {
                                if (annoch.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR)
                                {
                                    System.out.println("         is annotation member value pair");
                                    DetailAST pairIdent = annoch.findFirstToken(TokenTypes.IDENT);
                                    System.out.println("         pair ident " + pairIdent);

                                    if (pairIdent != null && pairIdent.getText().equals(NOGETTER))
                                    {
                                        System.out.println("         is noGetter");

                                        DetailAST expr = annoch.findFirstToken(TokenTypes.EXPR);
                                        if (expr != null)
                                        {
                                            System.out.println("         is expr");
                                            DetailAST val = expr.getFirstChild();

                                            //System.out.println("         val type " + val.getType());
                                            //System.out.println("         LITERAL_TRUE  " + TokenTypes.LITERAL_TRUE);
                                            //System.out.println("         LITERAL_FALSE " + TokenTypes.LITERAL_FALSE);


                                            if (val != null && val.getType() == TokenTypes.LITERAL_TRUE)
                                            {
                                                System.out.println("         is true " + val);
                                                noGetter = true;
                                            }
                                        }
                                    }
                                }
                                annoch = annoch.getNextSibling();
                            }
                        }
                    }

                    // final datamembers aren't checked
                    if (modifiers.branchContains(TokenTypes.FINAL))
                    {
                        noGetter = true;
                        varctr--;
                    }
                }

                // loop through all method definitions
                int getctr = 0;
                boolean getterfound = false;
                DetailAST child2 = objblock.getFirstChild();
                while (!noGetter && child2 != null)
                {
                    if (child2.getType() == TokenTypes.METHOD_DEF)
                    {
                        System.out.println("         methoddef");
                        boolean isgetter = isGetterMethod(child2);
                        System.out.println("         isgetter " + isgetter);

                        // is this method a getter?
                        if (isgetter)
                        {
                            getctr++;

                            // get the name of the getter
                            String gettername = "";
                            DetailAST ident2 = child2.findFirstToken(TokenTypes.IDENT);
                            if (ident2 != null)
                            {
                                gettername = ident2.getText();
                            }
                            System.out.println("         gettername " + gettername);

                            // a getter must start with "get"
                            if (gettername.startsWith("get"))
                            {
                                String shortname = gettername.substring(3);
                                System.out.println("         shortname " + shortname);
                                char letter = shortname.charAt(0);
                                String rest = shortname.substring(1);
                                System.out.println("         letter " + letter);
                                System.out.println("         rest   " + rest);

                                // the first letter after "get" must be uppercase
                                if (Character.isLetter(letter) && Character.isUpperCase(letter))
                                {
                                    // build the variable name from the getter identifier
                                    String lowername = Character.toLowerCase(letter) + rest;
                                    System.out.println("         lowername   " + lowername);

                                    // both names must be equal
                                    if (lowername.equals(varname))
                                    {
                                        System.out.println("         is getter");
                                        getterfound = true;
                                    }
                                }
                            }
                        }
                    }
                    child2 = child2.getNextSibling();
                }
                if (!noGetter && getctr == 0)
                {
                    log(child.getLineNo(),
                            child.getColumnNo() + aAST.getText().length() - 1,
                            CSCODE, varname);
                }
                if (!noGetter && !getterfound)
                {
                    log(child.getLineNo(),
                            child.getColumnNo() + aAST.getText().length() - 1,
                            CSCODE, varname);
                }
            }
            child = child.getNextSibling();
        }

        Actions as = BlueJManager.getInstance().getActions();
        Action a = as.search(CODE);
        if (a != null)
        {
            System.out.println("         action CODE found");
            if (a instanceof GetterCSAction gcsa)
            {
                gcsa.setNrVariables(varctr);
            }
        } else
        {
            System.out.println("         action CODE not found");
        }
    }

    /**
     * Returns whether an AST represents a setter method.
     *
     * @param aAST the AST to check with
     * @return whether the AST represents a setter method
     */
    private boolean isSetterMethod(final DetailAST aAST)
    {
        // Check have a method with exactly 7 children which are all that
        // is allowed in a proper setter method which does not throw any
        // exceptions.
        if ((aAST.getType() != TokenTypes.METHOD_DEF)
                || (aAST.getChildCount() != MAX_CHILDREN))
        {
            return false;
        }

        // Should I handle only being in a class????

        // Check the name matches format setX...
        final DetailAST type = aAST.findFirstToken(TokenTypes.TYPE);
        final String name = type.getNextSibling().getText();
        if (!name.matches("^set[A-Z].*"))
        { // Depends on JDK 1.4
            return false;
        }

        // Check the return type is void
        if (type.getChildCount(TokenTypes.LITERAL_VOID) == 0)
        {
            return false;
        }

        // Check that is had only one parameter
        final DetailAST params = aAST.findFirstToken(TokenTypes.PARAMETERS);
        if ((params == null)
                || (params.getChildCount(TokenTypes.PARAMETER_DEF) != 1))
        {
            return false;
        }

        // Now verify that the body consists of:
        // SLIST -> EXPR -> ASSIGN
        // SEMI
        // RCURLY
        final DetailAST slist = aAST.findFirstToken(TokenTypes.SLIST);
        if ((slist == null) || (slist.getChildCount() != BODY_SIZE))
        {
            return false;
        }

        final DetailAST expr = slist.getFirstChild();
        return (expr.getType() == TokenTypes.EXPR)
                && (expr.getFirstChild().getType() == TokenTypes.ASSIGN);
    }

    /**
     * Returns whether an AST represents a getter method.
     *
     * @param aAST the AST to check with
     * @return whether the AST represents a getter method
     */
    private boolean isGetterMethod(final DetailAST aAST)
    {
        System.out.println("isGetterMethod()");
        System.out.println("   aAST " + aAST);
        // Check have a method with exactly 7 children which are all that
        // is allowed in a proper getter method which does not throw any
        // exceptions.
        if ((aAST.getType() != TokenTypes.METHOD_DEF)
                || (aAST.getChildCount() != MAX_CHILDREN))
        {
            return false;
        }

        // Check the name matches format of getX or isX. Technically I should
        // check that the format isX is only used with a boolean type.
        final DetailAST type = aAST.findFirstToken(TokenTypes.TYPE);
        final String name = type.getNextSibling().getText();
        if (!name.matches("^(is|get)[A-Z].*"))
        { // Depends on JDK 1.4
            return false;
        }

        // Check the return type is void
        if (type.getChildCount(TokenTypes.LITERAL_VOID) > 0)
        {
            return false;
        }

        // Check that is had only one parameter
        final DetailAST params = aAST.findFirstToken(TokenTypes.PARAMETERS);
        if ((params == null)
                || (params.getChildCount(TokenTypes.PARAMETER_DEF) > 0))
        {
            return false;
        }

        // Now verify that the body consists of:
        // SLIST -> RETURN
        // RCURLY
        final DetailAST slist = aAST.findFirstToken(TokenTypes.SLIST);
        if ((slist == null) || (slist.getChildCount() != 2))
        {
            return false;
        }

        final DetailAST expr = slist.getFirstChild();
        return (expr.getType() == TokenTypes.LITERAL_RETURN)
                && (expr.getFirstChild().getType() == TokenTypes.EXPR);
    }
}

