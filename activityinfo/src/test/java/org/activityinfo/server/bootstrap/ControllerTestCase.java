/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package org.activityinfo.server.bootstrap;

import com.google.inject.Injector;
import org.activityinfo.server.auth.Authenticator;
import org.activityinfo.server.bootstrap.model.ConfirmInvitePageModel;
import org.activityinfo.server.bootstrap.model.HostPageModel;
import org.activityinfo.server.bootstrap.model.LoginPageModel;
import org.activityinfo.server.bootstrap.model.PageModel;
import org.activityinfo.server.dao.AuthenticationDAO;
import org.activityinfo.server.dao.UserDAO;
import org.activityinfo.server.domain.Authentication;
import org.activityinfo.server.domain.User;
import org.activityinfo.server.mock.MockHttpServletRequest;
import org.activityinfo.server.mock.MockHttpServletResponse;
import org.activityinfo.server.mock.MockTemplateConfiguration;
import org.easymock.IAnswer;
import org.junit.Before;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Alex Bertram
 */
public abstract class ControllerTestCase {
    protected MockHttpServletRequest req;
    protected MockHttpServletResponse resp;
    protected Injector injector;
    protected MockTemplateConfiguration templateCfg;
    protected AbstractController controller;

    protected static final String USER_EMAIL = "alex@bertram.com";
    protected static final String CORRECT_USER_PASS = "mypass";
    protected static final String WRONG_USER_PASS = "notmypass";

    protected static final String NEW_AUTH_TOKEN = "XYZ123";

    protected static final String NEW_USER_KEY = "ABC456";
    protected static final String NEW_USER_EMAIL = "bart@bart.nl";
    protected static final String BAD_KEY = "muwahaha";
    protected User newUser;
    protected static final String NEW_USER_NAME = "Henry";
    protected static final String NEW_USER_CHOSEN_LOCALE = "fr";

    protected static final String GOOD_AUTH_TOKEN = "BXD556";
    protected static final String BAD_AUTH_TOKEN = "NONSENSE";

    public ControllerTestCase() {
        newUser = new User();
    }

    @Before
    public final void setUpDependencies() {
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();

        User user = new User();
        user.setEmail(USER_EMAIL);

        newUser.setEmail(NEW_USER_EMAIL);
        newUser.setNewUser(true);
        newUser.setChangePasswordKey(NEW_USER_KEY);

        UserDAO userDAO = createMock(UserDAO.class);
        expect(userDAO.findUserByEmail(USER_EMAIL)).andReturn(user);
        expect(userDAO.findUserByEmail(not(eq(USER_EMAIL)))).andThrow(new NoResultException());

        expect(userDAO.findUserByChangePasswordKey(NEW_USER_KEY)).andReturn(newUser);
        expect(userDAO.findUserByChangePasswordKey(BAD_KEY)).andThrow(new NoResultException());
        replay(userDAO);

        Authentication auth = new Authentication(user);
        auth.setId(GOOD_AUTH_TOKEN);

        AuthenticationDAO authDAO = createNiceMock(AuthenticationDAO.class);
        expect(authDAO.findById(eq(GOOD_AUTH_TOKEN))).andReturn(auth);
        authDAO.persist(isA(Authentication.class));
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Authentication auth = (Authentication) getCurrentArguments()[0];
                auth.setId(NEW_AUTH_TOKEN);
                return null;
            }
        });
        replay(authDAO);

        Authenticator authenticator = createMock(Authenticator.class);
        expect(authenticator.check(eq(user), eq(WRONG_USER_PASS))).andReturn(false);
        expect(authenticator.check(eq(user), eq(CORRECT_USER_PASS))).andReturn(true);
        replay(authenticator);

        injector = createNiceMock(Injector.class);
        expect(injector.getInstance(AuthenticationDAO.class)).andReturn(authDAO);
        expect(injector.getInstance(Authenticator.class)).andReturn(authenticator);
        expect(injector.getInstance(UserDAO.class)).andReturn(userDAO);
        replay(injector);

        templateCfg = new MockTemplateConfiguration();
    }

    public void get() throws IOException, ServletException {
        controller.callGet(req, resp);
    }

    protected void get(String url) throws IOException, ServletException {
        req.setRequestURL(url);
        get();
    }

    protected void post() throws IOException, ServletException {
        controller.callPost(req, resp);
    }


    protected <T extends PageModel> void assertTemplateUsed(Class<T> modelclass) {
        assertEquals(PageModel.getTemplateName(modelclass), templateCfg.lastTemplateName);
    }

    private HostPageModel lastHostPageModel() {
        return ((HostPageModel) templateCfg.lastModel);
    }

    protected LoginPageModel lastLoginPageModel() {
        return (LoginPageModel) templateCfg.lastModel;
    }

    protected ConfirmInvitePageModel lastNewUserPageModel() {
        return (ConfirmInvitePageModel) templateCfg.lastModel;
    }
}
