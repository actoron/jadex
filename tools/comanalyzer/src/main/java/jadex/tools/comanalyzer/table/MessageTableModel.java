package jadex.tools.comanalyzer.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.collection.SortedList;
import jadex.tools.comanalyzer.Message;


/**
 *  The table model for messages. It uses a sorted list to store
 *  the messages, cause the sequence of messages must be conserved
 *  regardless of their insertion time.
 */
public class MessageTableModel extends AbstractTableModel
{
	// -------- constants --------

	/** List of all available column headers. */
	public static final List COLUMN_HEADERS;

	static
	{
		COLUMN_HEADERS = new ArrayList();

		COLUMN_HEADERS.add(new ColumnHeader("#", Message.SEQ_NO, Long.class));
		COLUMN_HEADERS.add(new ColumnHeader("Sender", Message.SENDER, IComponentIdentifier.class));
		COLUMN_HEADERS.add(new ColumnHeader("Receiver", Message.RECEIVER, IComponentIdentifier.class));
		COLUMN_HEADERS.add(new ColumnHeader("Peformative", Message.PERFORMATIVE, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Content", Message.CONTENT, Object.class));
		COLUMN_HEADERS.add(new ColumnHeader("Conversation", Message.CONVERSATION_ID, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Id", Message.XID, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Ontology", Message.ONTOLOGY, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Protocol", Message.PROTOCOL, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Receivers", Message.RECEIVERS, IComponentIdentifier[].class));

		COLUMN_HEADERS.add(new ColumnHeader("Encoding", Message.ENCODING, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Language", Message.LANGUAGE, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("In Reply To", Message.IN_REPLY_TO, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Reply By", Message.REPLY_BY, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Reply With", Message.REPLY_WITH, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Reply To", Message.REPLY_TO, String.class));
		
		COLUMN_HEADERS.add(new ColumnHeader("Date", Message.DATE, String.class));
		COLUMN_HEADERS.add(new ColumnHeader("Duration", Message.DURATION, Long.class));
//		COLUMN_HEADERS.add(new ColumnHeader("Content Start", Message.CONTENT_START, String.class));
//		COLUMN_HEADERS.add(new ColumnHeader("Content Class", Message.CONTENT_CLASS, String.class));
//		COLUMN_HEADERS.add(new ColumnHeader("Action Class", Message.ACTION_CLASS, String.class));
	}

	// -------- attributes --------

	/** SortedList of messages in table model. */
	protected SortedList messages = new SortedList();

	// -------- constructors --------

	// -------- MessageTableModel methods --------

	/**
	 * Add a message to the table
	 * @param message The message to add.
	 * @return <code>true</code> if success
	 */
	public boolean addMessage(Message message)
	{
		messages.add(message);
		int row = messages.size();
		fireTableRowsInserted(row, row);
		return true;
	}

	/**
	 * Remove a message from the table.
	 * @param message The message to remove.
	 * @return <code>true</code> if success
	 */
	public boolean removeMessage(Message message)
	{
		int row = messages.indexOf(message);
		messages.remove(message);
		fireTableRowsDeleted(row, row);
		return true;
	}

	/**
	 * Add an array of message to the table.
	 * @param messages The Array of messages to add.
	 */
	public void addMessages(Message[] messages)
	{
		this.messages.addAll(SUtil.arrayToList(messages));
		fireTableDataChanged();
	}

	/**
	 * Remove all messages from table.
	 */
	public void removeAllMessages()
	{
		messages.clear();
		fireTableDataChanged();
	}

	// -------- TableModel methods --------

	/**
	 * Returns the number of columns in the model 
	 * @return The number of columns in the model
	 */
	public int getColumnCount()
	{
		return COLUMN_HEADERS.size();
	}

	/**
	 * Returns the number of rows in the model.
	 * @return the number of rows in the model
	 */
	public int getRowCount()
	{
		return messages != null ? messages.size() : 0;
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 *
	 * @param	rowIndex	the row whose value is to be queried
	 * @param	columnIndex 	the column whose value is to be queried
	 * @return	the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(messages == null || rowIndex < 0 || rowIndex >= messages.size())
		{
			return null;
		}
		Message me = (Message)messages.get(rowIndex);
		ColumnHeader header = (ColumnHeader)COLUMN_HEADERS.get(columnIndex);
		return me.getParameter(header.getParamName());
	}

	/**
	 *  Returns the name for the column.
	 * @param column  The column being queried
	 * @return A string containing the default name of <code>column</code>
	 */
	public String getColumnName(int columnIndex)
	{
		ColumnHeader header = (ColumnHeader)COLUMN_HEADERS.get(columnIndex);
		return header.getColumnName();
	}

	/**
	 *  Returns the class of column <code>columnIndex</code>.
	 *  @param columnIndex  The column being queried
	 *  @return the class of column <code>columnIndex</code>
	 */
	public Class getColumnClass(int columnIndex)
	{
		ColumnHeader header = (ColumnHeader)COLUMN_HEADERS.get(columnIndex);
		return header.getColumnClass();
	}

	/**
	 * Returns <code>true</code> if the message is in the table
	 * @param message The message queried
	 * @return <code>true</code> if the message exist
	 */
	public boolean containsMessage(Message message)
	{
		return messages.contains(message);
	}

	/**
	 * Returns the message at row i.
	 * @param i The row with the message.
	 * @return The message at row i.
	 */
	public Message getMessage(int i)
	{
		return messages == null || i < 0 || i >= messages.size() ? null : (Message)messages.get(i);
	}

	// -------- inner classes --------

	/**
	 * A class for column headers.
	 * The name for the column, the key for the message parameter
	 * and the class of the returntype of the parameter are provided.
	 */
	private static class ColumnHeader
	{

		/** The column name */
		private String columnName;

		/** The parameter name */
		private String paramName;

		/** The column class */
		private Class columnClass;

		/**
		 * @param columnName The name of the column.
		 * @param paramName The name of the message parameter.
		 * @param columnClass The class of the column.
		 */
		public ColumnHeader(String columnName, String paramName, Class columnClass)
		{
			this.columnName = columnName;
			this.paramName = paramName;
			this.columnClass = columnClass;
		}

		/**
		 * @return The column class.
		 */
		public Class getColumnClass()
		{
			return columnClass;
		}

		/**
		 * @return The column name.
		 */
		public String getColumnName()
		{
			return columnName;
		}

		/**
		 * @return The message parameter key for the column.
		 */
		public String getParamName()
		{
			return paramName;
		}

	}
}