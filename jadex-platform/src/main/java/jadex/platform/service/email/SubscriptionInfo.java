package jadex.platform.service.email;

import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.commons.IFilter;
import jadex.commons.SUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.search.FlagTerm;

/**
 *  Info struct for email subscriptions.  
 */
public class SubscriptionInfo
{
	//-------- attributes --------
	
	/** The filter. */
	protected IFilter<Email> filter;
	
	/** The account. */
	protected EmailAccount account;

	/** The number of messages in the folder. */
	protected int total;
	protected int lastseenno;
	protected Email lastseenmsg;
	
	//-------- constructors --------
	
	/**
	 *  Create a new subscription info.
	 */
	public SubscriptionInfo(IFilter<Email> filter, EmailAccount account)
	{
		this.total = 0;
		this.filter = filter;
		this.account = account;
	}

	//-------- methods --------
	
	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter<Email> getFilter()
	{
		return filter;
	}

	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 */
	public void setFilter(IFilter<Email> filter)
	{
		this.filter = filter;
	}

	/**
	 *  Get the account.
	 *  @return The account.
	 */
	public EmailAccount getAccount()
	{
		return account;
	}

	/**
	 *  Set the account.
	 *  @param account The account to set.
	 */
	public void setAccount(EmailAccount account)
	{
		this.account = account;
	}
	
	/**
	 *  Fetch new emails from the inbox.
	 */
	public List<Email> getNewEmails()
	{
		List<Email> ret = null;
		
		Store store = null;
		try
		{
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", account.getReceiveProtocol());
			Session session = Session.getDefaultInstance(props, null);
			store = session.getStore(account.getReceiveProtocol());
			store.connect(account.getReceiveHost(), account.getUser(), account.getPassword());
			Folder f = store.getFolder("inbox");
			f.open(Folder.READ_ONLY);
			int newtotal = f.getMessageCount();
			
			// Rescan messages 
			if(lastseenno>0 && total>0)
			{
				// if less messages than last time
				if(lastseenno>newtotal)
				{
					total = 0;
				}
				else
				{
					// if the position of the last seen message has changed
					Message lsm = f.getMessage(lastseenno);
					if(lsm!=null && !convertMessage(lsm).equals(lastseenmsg))
					{
						total = 0;
					}
				}
			}
			
			if(total<newtotal)
			{
				Message[] msgs = f.getMessages(total+1, newtotal);
				total = newtotal;
				FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
				Message messages[] = f.search(ft, msgs);
				
				if(messages!=null && messages.length>0)
				{
					Email email = null;
					ret = new ArrayList<Email>();
					for(int i=0; i<messages.length; i++)
					{
						email = convertMessage(messages[i]);
						if(filter==null || filter.filter(email))
						{
							ret.add(email);
						}
					}
					
					// check if lastseenmsg is part of list
					if(lastseenmsg!=null)
					{
						int idx = ret.indexOf(lastseenmsg);
						if(idx!=-1 && idx+1<ret.size())
						{
							// cut off old messages
							ret = ret.subList(idx+1, ret.size()-1);
						}
					}
					
					lastseenno = messages[messages.length-1].getMessageNumber();
					lastseenmsg = email;
				}
			}
		}
		catch(Exception e)
		{
			throw e instanceof RuntimeException? (RuntimeException)e: new RuntimeException(e);
//			e.printStackTrace();
		}
		finally
		{
			if(store!=null)
			{
				try
				{
					store.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert an email message to the simple jadex email format.
	 */
	protected Email convertMessage(Message msg)
	{
		Email ret = null;
		try
		{
			ret = new Email();
			ret.setSubject(msg.getSubject());
            ret.setSender(msg.getFrom()[0].toString());
           
            Address[] tos = msg.getRecipients(RecipientType.TO);
            Address[] ccs = msg.getRecipients(RecipientType.CC);
            Address[] bccs = msg.getRecipients(RecipientType.BCC);
                       
            ret.setReceivers(convertAddresses(tos));
            ret.setCcs(convertAddresses(ccs));
            ret.setBccs(convertAddresses(bccs));
            
            Object content = msg.getContent();
            if(content instanceof String)
            {
            	ret.setContent((String)content);
            }
            else if(content instanceof Multipart) 
            {
            	List<String> texts = new ArrayList<String>();
            	List<Object> atts = new ArrayList<Object>();
            	collectParts((Multipart)content, texts, atts);
            	
            	if(texts.size()>0)
            	{
	            	StringBuffer buf = new StringBuffer();
	            	for(int i=0; i<texts.size(); i++)
	            	{
	            		buf.append(texts.get(i));
	            		if(i+1<texts.size())
	            		{
	            			buf.append("--------").append(SUtil.LF);
	            		}
	            	}
	            	ret.setContent(buf.toString());
            	}
            	
            	if(atts.size()>0)
            	{
            		ret.setAttachments(atts.toArray());
            	}
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected void collectParts(Multipart multipart, List<String> contents, List<Object> attachments) 
		throws MessagingException, IOException
	{
		int parts = multipart.getCount();
		for(int i = 0; i < parts; i++)
		{
			MimeBodyPart part = (MimeBodyPart)multipart.getBodyPart(i);
			if(part.getContent() instanceof Multipart)
			{
				collectParts((Multipart)part.getContent(), contents, attachments);
			}
			else
			{
				if(part.isMimeType("text/plain"))
				{
					contents.add((String)part.getContent());
				}
				else if(part.isMimeType("text/html"))
				{
					// todo: html content
				}
				else
				{
					// binary content is given as inputstream
//					System.out.println("non text: "+part.getContentType()+" "+part.getContent());
					attachments.add(part.getContent());
					// Try to get the name of the attachment
//					extension = part.getDataHandler().getName();
//					FileOutputStream out = new FileOutputStream(new File(
//							filename));
//					FileInputStream in = part.getInputStream();
//					int k;
//					while((k = in.read()) != -1)
//					{
//						out.write(k);
//					}
				}
			}
		}
	}
	
	/**
	 *  Convert email addresses to strings.
	 */
	protected String[] convertAddresses(Address[] addrs)
	{
		String[] ret = null;
		if(addrs!=null)
		{
			ret = new String[addrs.length];
			for(int i=0; i<addrs.length; i++)
			{
				ret[i] = addrs[i].toString();
			}
		}
		return ret;
	}
}

