/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jadex.commons.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.harmony.beans.internal.nls.Messages;

import jadex.commons.beans.beancontext.BeanContext;

/**
 * This class <code>Beans</code> provides some methods for manipulting bean
 * controls.
 * 
 */

public class Beans {

    private static boolean designTime = false;

    private static boolean guiAvailable = true;

    /**
     * Constructs a Beans instance.
     */
    public Beans() {
        // expected
    }

    @SuppressWarnings("unchecked")
    private static Object internalInstantiate(ClassLoader cls, String beanName,
            BeanContext context) throws IOException,
            ClassNotFoundException {
        // First try to load it from a serialization file.
        String beanResourceName = getBeanResourceName(beanName);
        InputStream is = (cls == null) ? ClassLoader
                .getSystemResourceAsStream(beanResourceName) : cls
                .getResourceAsStream(beanResourceName);

        IOException serializationException = null;
        Object result = null;
        if (is != null) {
            try {
                ObjectInputStream ois = (cls == null) ? new ObjectInputStream(
                        is) : new CustomizedObjectInputStream(is, cls);
                result = ois.readObject();
                ois.close();
            } catch (IOException e) {
                // Not loadable - remember this as we may throw it later.
                serializationException = e;
            }
        }

        // If it didn't work, try to instantiate it from the given classloader
        boolean deserialized = true;
        ClassLoader classLoader = cls == null ? ClassLoader
                .getSystemClassLoader() : cls;
        if (result == null) {
            deserialized = false;
            try {
                result = Class.forName(beanName, true, classLoader)
                        .newInstance();
            } catch (Exception e) {
                if (serializationException != null) {
                    throw serializationException;
                }
                throw new ClassNotFoundException(e.getClass() + ": " //$NON-NLS-1$
                        + e.getMessage());
            }
        }

        if (result != null) {
            // Applet specific initialization
            if (null != context) {
                context.add(result);
            }
        }
        return result;
    }

    /**
     * Obtains an instance of a JavaBean specified the bean name using the
     * specified class loader.
     * <p>
     * If the specified class loader is null, the system class loader is used.
     * </p>
     * 
     * @param loader
     *            the specified class loader. It can be null.
     * @param name
     *            the name of the JavaBean
     * @return an isntance of the bean.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object instantiate(ClassLoader loader, String name)
            throws IOException, ClassNotFoundException {
        return internalInstantiate(loader, name, null);
    }

    /**
     * Obtains an instance of a JavaBean specified the bean name using the
     * specified class loader, and adds the instance into the specified bean
     * context.
     * 
     * <p>
     * If the specified class loader is null, the system class loader is used.
     * </p>
     * 
     * @param cls
     *            the specified class loader. It can be null.
     * @param beanName
     *            the name of the JavaBean
     * @param beanContext
     *            the beancontext in which the bean instance will be added.
     * @return an instance of the specified JavaBean.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object instantiate(ClassLoader cls, String beanName,
			BeanContext beanContext) throws IOException, ClassNotFoundException {
		return internalInstantiate(cls, beanName, beanContext);

	}

    /**
     * Obtain an alternative type view of the given bean. The view type is
     * specified by the parameter <code>type</code>.
     * <p>
     * If the type view cannot be obtained, the original bean object is
     * returned.
     * </p>
     * 
     * @param bean
     *            the original bean object.
     * @param targetType
     *            the specified view type.
     * @return a type view of the given bean.
     */
    public static Object getInstanceOf(Object bean, Class<?> targetType) {
        return bean;
    }

    /**
     * Determine if the the specified bean object can be viewed as the specified
     * type.
     * 
     * @param bean
     *            the specified bean object.
     * @param targetType
     *            the specifed view type.
     * @return true if the specified bean object can be viewed as the specified
     *         type; otherwise, return false;
     */
    public static boolean isInstanceOf(Object bean, Class<?> targetType) {
        if (bean == null) {
            throw new NullPointerException(Messages.getString("beans.1D")); //$NON-NLS-1$
        }

        return targetType == null ? false : targetType.isInstance(bean);
    }

    /**
     * Set whether or not a GUI is available in the bean's current environment.
     * 
     * @param isGuiAvailable
     *            should be <code>true</code> to signify that a GUI is
     *            available, <code>false</code> otherwise.
     * @throws SecurityException
     *             if the caller does not have the required permission to access
     *             or modify system properties.
     */
    public static synchronized void setGuiAvailable(boolean isGuiAvailable)
            throws SecurityException {
        checkPropertiesAccess();
        guiAvailable = isGuiAvailable;
    }
    
    /**
     * Used to indicate whether of not it's in an application construction
     * environment.
     * 
     * @param isDesignTime
     *            true to indicate that it's in application construction
     *            environment.
     * @throws SecurityException
     */
    public static void setDesignTime(boolean isDesignTime)
            throws SecurityException {
        checkPropertiesAccess();
        synchronized(Beans.class){
            designTime = isDesignTime;
        }
    }

    /**
     * Returns a boolean indication of whether or not a GUI is available for
     * beans.
     * 
     * @return <code>true</code> if a GUI is available, otherwise
     *         <code>false</code>.
     */
    public static synchronized boolean isGuiAvailable() {
        return guiAvailable;
    }

    /**
     * Determine if it's in design-mode.
     * 
     * @return true if it's in an application construction environment.
     */
    public static synchronized boolean isDesignTime() {
        return designTime;
    }

    private static void checkPropertiesAccess() throws SecurityException {
        SecurityManager sm = System.getSecurityManager();

        if (sm != null) {
            sm.checkPropertiesAccess();
        }
    }

    private static String getBeanResourceName(String beanName) {
        return beanName.replace('.', '/') + ".ser"; //$NON-NLS-1$
    }
    
	//  Maps malformed URL exception to ClassNotFoundException
	private static URL safeURL(String urlString) throws ClassNotFoundException {
		try {
			return new URL(urlString);
		} catch (MalformedURLException exception) {
			throw new ClassNotFoundException(exception.getMessage());
		}
	}

}
