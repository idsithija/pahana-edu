package com.pahanaedu.util;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Session Utility Tests")
class SessionUtilTest extends BaseTestCase {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpSession session;
    
    private User adminUser;
    private User operatorUser;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        
        // Create test users
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRole(User.UserRole.ADMIN);
        adminUser.setPasswordHash("hashedpassword");
        
        operatorUser = new User();
        operatorUser.setUsername("operator");
        operatorUser.setRole(User.UserRole.OPERATOR);
        operatorUser.setPasswordHash("hashedpassword");
    }
    
    @Nested
    @DisplayName("Session Creation Tests")
    class SessionCreationTests {
        
        @Test
        @DisplayName("Should create user session successfully")
        void testCreateUserSession() {
            // Arrange
            when(request.getSession(true)).thenReturn(session);
            
            // Act
            SessionUtil.createUserSession(request, adminUser);
            
            // Assert
            verify(session).setAttribute(SessionUtil.CURRENT_USER_ATTR, adminUser);
            verify(session).setAttribute(eq(SessionUtil.LOGIN_TIME_ATTR), any(LocalDateTime.class));
            verify(session).setAttribute(eq(SessionUtil.LAST_ACTIVITY_ATTR), any(LocalDateTime.class));
            verify(session).setMaxInactiveInterval(30 * 60); // 30 minutes
        }
        
        @Test
        @DisplayName("Should throw exception for null request in session creation")
        void testCreateUserSessionNullRequest() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                SessionUtil.createUserSession(null, adminUser);
            });
            
            assertTrue(exception.getMessage().contains("Request and user cannot be null"));
        }
        
        @Test
        @DisplayName("Should throw exception for null user in session creation")
        void testCreateUserSessionNullUser() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                SessionUtil.createUserSession(request, null);
            });
            
            assertTrue(exception.getMessage().contains("Request and user cannot be null"));
        }
    }
    
    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {
        
        @Test
        @DisplayName("Should get current user from session")
        void testGetCurrentUser() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            
            // Act
            User result = SessionUtil.getCurrentUser(request);
            
            // Assert
            assertNotNull(result);
            assertEquals(adminUser, result);
        }
        
        @Test
        @DisplayName("Should return null when no session exists")
        void testGetCurrentUserNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act
            User result = SessionUtil.getCurrentUser(request);
            
            // Assert
            assertNull(result);
        }
        
        @Test
        @DisplayName("Should return null for null request")
        void testGetCurrentUserNullRequest() {
            // Act
            User result = SessionUtil.getCurrentUser(null);
            
            // Assert
            assertNull(result);
        }
        
        @Test
        @DisplayName("Should get username correctly")
        void testGetUsername() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            
            // Act
            String result = SessionUtil.getUsername(request);
            
            // Assert
            assertEquals("admin", result);
        }
        
        @Test
        @DisplayName("Should return null username when no user in session")
        void testGetUsernameNoUser() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(null);
            
            // Act
            String result = SessionUtil.getUsername(request);
            
            // Assert
            assertNull(result);
        }
    }
    
    @Nested
    @DisplayName("Authentication Checks Tests")
    class AuthenticationChecksTests {
        
        @Test
        @DisplayName("Should check if user is authenticated")
        void testIsUserAuthenticated() {
            // Arrange - User is authenticated
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            
            // Act & Assert
            assertTrue(SessionUtil.isUserAuthenticated(request));
        }
        
        @Test
        @DisplayName("Should return false when user is not authenticated")
        void testIsUserNotAuthenticated() {
            // Arrange - No user in session
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(null);
            
            // Act & Assert
            assertFalse(SessionUtil.isUserAuthenticated(request));
        }
        
        @Test
        @DisplayName("Should check if current user is admin")
        void testIsCurrentUserAdmin() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            
            // Act & Assert
            assertTrue(SessionUtil.isCurrentUserAdmin(request));
            assertTrue(SessionUtil.isAdmin(request)); // Test alias method
        }
        
        @Test
        @DisplayName("Should return false when current user is not admin")
        void testIsCurrentUserNotAdmin() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(operatorUser);
            
            // Act & Assert
            assertFalse(SessionUtil.isCurrentUserAdmin(request));
            assertFalse(SessionUtil.isAdmin(request)); // Test alias method
        }
        
        @Test
        @DisplayName("Should check if current user is operator")
        void testIsCurrentUserOperator() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(operatorUser);
            
            // Act & Assert
            assertTrue(SessionUtil.isCurrentUserOperator(request));
        }
        
        @Test
        @DisplayName("Should return false when current user is not operator")
        void testIsCurrentUserNotOperator() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            
            // Act & Assert
            assertFalse(SessionUtil.isCurrentUserOperator(request));
        }
        
        @Test
        @DisplayName("Should return false for role checks when no user in session")
        void testRoleChecksNoUser() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(null);
            
            // Act & Assert
            assertFalse(SessionUtil.isCurrentUserAdmin(request));
            assertFalse(SessionUtil.isCurrentUserOperator(request));
            assertFalse(SessionUtil.isAdmin(request));
        }
    }
    
    @Nested
    @DisplayName("Session Activity Tests")
    class SessionActivityTests {
        
        @Test
        @DisplayName("Should update last activity time")
        void testUpdateLastActivity() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            
            // Act
            SessionUtil.updateLastActivity(request);
            
            // Assert
            verify(session).setAttribute(eq(SessionUtil.LAST_ACTIVITY_ATTR), any(LocalDateTime.class));
        }
        
        @Test
        @DisplayName("Should handle update last activity with null request")
        void testUpdateLastActivityNullRequest() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                SessionUtil.updateLastActivity(null);
            });
        }
        
        @Test
        @DisplayName("Should handle update last activity with no session")
        void testUpdateLastActivityNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act & Assert
            assertDoesNotThrow(() -> {
                SessionUtil.updateLastActivity(request);
            });
        }
        
        @Test
        @DisplayName("Should get login time")
        void testGetLoginTime() {
            // Arrange
            LocalDateTime loginTime = LocalDateTime.now();
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.LOGIN_TIME_ATTR)).thenReturn(loginTime);
            
            // Act
            LocalDateTime result = SessionUtil.getLoginTime(request);
            
            // Assert
            assertEquals(loginTime, result);
        }
        
        @Test
        @DisplayName("Should return null login time when no session")
        void testGetLoginTimeNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act
            LocalDateTime result = SessionUtil.getLoginTime(request);
            
            // Assert
            assertNull(result);
        }
        
        @Test
        @DisplayName("Should get last activity time")
        void testGetLastActivity() {
            // Arrange
            LocalDateTime lastActivity = LocalDateTime.now();
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.LAST_ACTIVITY_ATTR)).thenReturn(lastActivity);
            
            // Act
            LocalDateTime result = SessionUtil.getLastActivity(request);
            
            // Assert
            assertEquals(lastActivity, result);
        }
        
        @Test
        @DisplayName("Should return null last activity when no session")
        void testGetLastActivityNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act
            LocalDateTime result = SessionUtil.getLastActivity(request);
            
            // Assert
            assertNull(result);
        }
    }
    
    @Nested
    @DisplayName("Session Invalidation Tests")
    class SessionInvalidationTests {
        
        @Test
        @DisplayName("Should invalidate session successfully")
        void testInvalidateSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            
            // Act
            SessionUtil.invalidateSession(request);
            
            // Assert
            verify(session).invalidate();
        }
        
        @Test
        @DisplayName("Should handle invalidate session with null request")
        void testInvalidateSessionNullRequest() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                SessionUtil.invalidateSession(null);
            });
        }
        
        @Test
        @DisplayName("Should handle invalidate session with no session")
        void testInvalidateSessionNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act & Assert
            assertDoesNotThrow(() -> {
                SessionUtil.invalidateSession(request);
            });
        }
    }
    
    @Nested
    @DisplayName("URL Handling Tests")
    class URLHandlingTests {
        
        @Test
        @DisplayName("Should store original request URL")
        void testStoreOriginalRequestURL() {
            // Arrange
            String originalUrl = "/dashboard";
            when(request.getSession(true)).thenReturn(session);
            
            // Act
            SessionUtil.storeOriginalRequestURL(request, originalUrl);
            
            // Assert
            verify(session).setAttribute(SessionUtil.ORIGINAL_REQUEST_URL_ATTR, originalUrl);
        }
        
        @Test
        @DisplayName("Should handle store URL with null request")
        void testStoreOriginalRequestURLNullRequest() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                SessionUtil.storeOriginalRequestURL(null, "/dashboard");
            });
        }
        
        @Test
        @DisplayName("Should handle store URL with null URL")
        void testStoreOriginalRequestURLNullUrl() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                SessionUtil.storeOriginalRequestURL(request, null);
            });
        }
        
        @Test
        @DisplayName("Should get and remove original request URL")
        void testGetAndRemoveOriginalRequestURL() {
            // Arrange
            String originalUrl = "/dashboard";
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.ORIGINAL_REQUEST_URL_ATTR)).thenReturn(originalUrl);
            
            // Act
            String result = SessionUtil.getAndRemoveOriginalRequestURL(request);
            
            // Assert
            assertEquals(originalUrl, result);
            verify(session).removeAttribute(SessionUtil.ORIGINAL_REQUEST_URL_ATTR);
        }
        
        @Test
        @DisplayName("Should return null when getting URL with no session")
        void testGetAndRemoveOriginalRequestURLNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act
            String result = SessionUtil.getAndRemoveOriginalRequestURL(request);
            
            // Assert
            assertNull(result);
        }
        
        @Test
        @DisplayName("Should return null when getting URL with null request")
        void testGetAndRemoveOriginalRequestURLNullRequest() {
            // Act
            String result = SessionUtil.getAndRemoveOriginalRequestURL(null);
            
            // Assert
            assertNull(result);
        }
    }
    
    @Nested
    @DisplayName("Session Information Tests")
    class SessionInformationTests {
        
        @Test
        @DisplayName("Should get session information with user")
        void testGetSessionInfoWithUser() {
            // Arrange
            LocalDateTime loginTime = LocalDateTime.of(2023, 6, 15, 10, 30, 0);
            LocalDateTime lastActivity = LocalDateTime.of(2023, 6, 15, 11, 0, 0);
            String sessionId = "SESSION123";
            
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(adminUser);
            when(session.getAttribute(SessionUtil.LOGIN_TIME_ATTR)).thenReturn(loginTime);
            when(session.getAttribute(SessionUtil.LAST_ACTIVITY_ATTR)).thenReturn(lastActivity);
            when(session.getId()).thenReturn(sessionId);
            
            // Act
            String result = SessionUtil.getSessionInfo(request);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.contains("admin"));
            assertTrue(result.contains("2023-06-15T10:30"));
            assertTrue(result.contains("2023-06-15T11:00"));
            assertTrue(result.contains("SESSION123"));
        }
        
        @Test
        @DisplayName("Should get session information without user")
        void testGetSessionInfoWithoutUser() {
            // Arrange
            String sessionId = "SESSION123";
            
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute(SessionUtil.CURRENT_USER_ATTR)).thenReturn(null);
            when(session.getAttribute(SessionUtil.LOGIN_TIME_ATTR)).thenReturn(null);
            when(session.getAttribute(SessionUtil.LAST_ACTIVITY_ATTR)).thenReturn(null);
            when(session.getId()).thenReturn(sessionId);
            
            // Act
            String result = SessionUtil.getSessionInfo(request);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.contains("None"));
            assertTrue(result.contains("Unknown"));
            assertTrue(result.contains("SESSION123"));
        }
        
        @Test
        @DisplayName("Should handle session info with no session")
        void testGetSessionInfoNoSession() {
            // Arrange
            when(request.getSession(false)).thenReturn(null);
            
            // Act
            String result = SessionUtil.getSessionInfo(request);
            
            // Assert
            assertEquals("No session", result);
        }
        
        @Test
        @DisplayName("Should handle session info with null request")
        void testGetSessionInfoNullRequest() {
            // Act
            String result = SessionUtil.getSessionInfo(null);
            
            // Assert
            assertEquals("No request", result);
        }
    }
    
    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {
        
        @Test
        @DisplayName("Should have correct session attribute constants")
        void testSessionAttributeConstants() {
            assertEquals("currentUser", SessionUtil.CURRENT_USER_ATTR);
            assertEquals("loginTime", SessionUtil.LOGIN_TIME_ATTR);
            assertEquals("lastActivity", SessionUtil.LAST_ACTIVITY_ATTR);
            assertEquals("originalRequestURL", SessionUtil.ORIGINAL_REQUEST_URL_ATTR);
        }
    }
}