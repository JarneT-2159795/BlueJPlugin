package org.bluejplugin;

import bluej.extensions2.editor.TextLocation;

import java.net.URL;
import java.util.Comparator;

/**
 * Class Comment contains the location and the suggestion for the errors
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (08/05/2013)
 */
public class Comment
{
    private final String commentText;
    private TextLocation textLocation;
    private URL url;

    /**
     * Constructor for this class
     *
     * @param text     The suggestion about an error
     * @param location The location of the error
     */
    public Comment(String text, TextLocation location)
    {
        commentText = text;
        textLocation = location;
        url = null;
    }

    /**
     * Constructor for this class
     *
     * @param text     The suggestion about an error
     * @param location The location of the error
     * @param url      The url to the PMD docs
     */
    public Comment(String text, TextLocation location, URL url)
    {
        commentText = text;
        textLocation = location;
        this.url = url;
    }

    /**
     * Constructor for this class
     *
     * @param text The suggestion about an error
     */
    public Comment(String text)
    {
        commentText = text;
    }

    /**
     * Methode to return the suggestion to the according error
     *
     * @return The suggestion about an error
     */
    public String getComment()
    {
        return commentText;
    }

    /**
     * Methode to return the location of the error
     *
     * @return The location of the error
     */
    public TextLocation getLocation()
    {
        return textLocation;
    }

    /**
     * Method to return the rowNumber of the original location of the error
     *
     * @return The RowNumber
     */
    public int getRowNumber()
    {
        return textLocation.getLine();
    }

    /**
     * Method to return the url to the PMD docs
     *
     * @return The url
     */
    public URL getUrl()
    {
        return url;
    }

    /**
     * Method to request the string containing the comment
     *
     * @return The comment
     */
    @Override
    public String toString()
    {
        return commentText;
    }
}

/**
 * class to sort the arrayList of comments, according to rowNumber
 *
 * @author Hermans
 */
class commentLocationComparator implements Comparator<Comment>
{
    @Override
    public int compare(Comment comment1, Comment comment2)
    {
        return comment1.getRowNumber() - comment2.getRowNumber();
    }
}
