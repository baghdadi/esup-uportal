/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */
package org.jasig.portal.security.provider;


import org.jasig.portal.AuthorizationException;
import org.jasig.portal.security.IAuthorizationService;
import org.jasig.portal.security.IAuthorizationServiceFactory;

/**
 * <p>The factory class for the uPortal reference 
 * IAuthorizationService implementation.</p>
 *
 * @author Dan Ellentuck
 * @version $Revision$
 */
public class AuthorizationServiceFactoryImpl 
    implements IAuthorizationServiceFactory {
    
  public IAuthorizationService getAuthorization() 
      throws AuthorizationException {
    return AuthorizationImpl.singleton();
  }
}