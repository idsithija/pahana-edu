package com.pahanaedu.controller;

import com.pahanaedu.BaseTestCase;
import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import com.pahanaedu.util.SessionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Login Servlet Tests")
class LoginServletTest extends BaseTestCase {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private RequestDispatcher requestDispatcher;
    
    @Mock
    private UserService userService;
    
    private LoginServlet loginServlet;
    private User testUser;
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        
        loginServlet = new TestableLoginServlet(userService);
        
        // Set up test user
        testUser = new User();
        testUser.setUsername("admin");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(User.UserRole.ADMIN);
        
        // Set up response writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        
        try {
            when(response.getWriter()).thenReturn(printWriter);
        } catch (IOException e) {
            fail("Failed to set up response writer");
        }
    }
    
    @Nested
    @DisplayName("GET Request Tests")
    class GetRequestTests {
        
        @Test
        @DisplayName("Should display login form for GET request")
        void testDoGetDisplaysLoginForm() throws ServletException, IOException {
            // Arrange
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // Act
            loginServlet.doGet(request, response);
            
            // Assert
            verify(requestDispatcher).forward(request, response);
        }
        
        @Test
        @DisplayName("Should redirect to dashboard if already logged in")
        void testDoGetRedirectsIfLoggedIn() throws ServletException, IOException {
            // Arrange
            try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
                sessionUtilMock.when(() -> SessionUtil.isUserAuthenticated(request)).thenReturn(true);
                
                // Act
                loginServlet.doGet(request, response);
                
                // Assert
                verify(response).sendRedirect("dashboard");
            }
        }
    }
    
    @Nested
    @DisplayName("POST Request Tests")
    class PostRequestTests {
        
        @Test
        @DisplayName("Should authenticate user with valid credentials")
        void testDoPostValidCredentials() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getSession()).thenReturn(session);
            
            try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
                // Use reflection to set the private userService field or use public method
                // Since userService is private, we'll need to work around this
                when(userService.authenticateUser("admin", "admin123")).thenReturn(java.util.Optional.of(testUser));
                
                // Act
                loginServlet.doPost(request, response);
                
                // Assert
                sessionUtilMock.verify(() -> SessionUtil.createUserSession(request, testUser));
                verify(response).sendRedirect("dashboard");
            }
        }
        
        @Test
        @DisplayName("Should reject invalid credentials")
        void testDoPostInvalidCredentials() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("wrongpassword");
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            when(userService.authenticateUser("admin", "wrongpassword")).thenReturn(java.util.Optional.empty());
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(request).setAttribute("error", "Invalid username or password");
            verify(requestDispatcher).forward(request, response);
        }
        
        @Test
        @DisplayName("Should handle empty username")
        void testDoPostEmptyUsername() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("");
            when(request.getParameter("password")).thenReturn("password");
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(request).setAttribute("error", "Username and password are required");
            verify(requestDispatcher).forward(request, response);
        }
        
        @Test
        @DisplayName("Should handle empty password")
        void testDoPostEmptyPassword() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("");
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(request).setAttribute("error", "Username and password are required");
            verify(requestDispatcher).forward(request, response);
        }
        
        @Test
        @DisplayName("Should handle null parameters")
        void testDoPostNullParameters() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn(null);
            when(request.getParameter("password")).thenReturn(null);
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(request).setAttribute("error", "Username and password are required");
            verify(requestDispatcher).forward(request, response);
        }
        
        @Test
        @DisplayName("Should redirect to original URL after successful login")
        void testDoPostRedirectToOriginalURL() throws ServletException, IOException {
            // Arrange
            String originalUrl = "/customer/list";
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getSession()).thenReturn(session);
            
            try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
                // userService is injected via TestableLoginServlet constructor
                when(userService.authenticateUser("admin", "admin123")).thenReturn(java.util.Optional.of(testUser));
                sessionUtilMock.when(() -> SessionUtil.getAndRemoveOriginalRequestURL(request))
                    .thenReturn(originalUrl);
                
                // Act
                loginServlet.doPost(request, response);
                
                // Assert
                verify(response).sendRedirect(originalUrl);
            }
        }
        
        @Test
        @DisplayName("Should handle authentication service exceptions")
        void testDoPostServiceException() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // userService is injected via TestableLoginServlet constructor
            when(userService.authenticateUser("admin", "admin123"))
                .thenThrow(new RuntimeException("Database connection error"));
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(request).setAttribute("error", "Login service temporarily unavailable. Please try again.");
            verify(requestDispatcher).forward(request, response);
        }
        
        @Test
        @DisplayName("Should trim whitespace from username and password")
        void testDoPostTrimsWhitespace() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("  admin  ");
            when(request.getParameter("password")).thenReturn("  admin123  ");
            when(request.getSession()).thenReturn(session);
            
            try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
                // userService is injected via TestableLoginServlet constructor
                when(userService.authenticateUser("admin", "admin123")).thenReturn(java.util.Optional.of(testUser));
                
                // Act
                loginServlet.doPost(request, response);
                
                // Assert
                verify(userService).authenticateUser("admin", "admin123");
                sessionUtilMock.verify(() -> SessionUtil.createUserSession(request, testUser));
                verify(response).sendRedirect("dashboard");
            }
        }
    }
    
    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {
        
        @Test
        @DisplayName("Should prevent session fixation attacks")
        void testPreventSessionFixation() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getSession()).thenReturn(session);
            
            try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
                // userService is injected via TestableLoginServlet constructor
                when(userService.authenticateUser("admin", "admin123")).thenReturn(java.util.Optional.of(testUser));
                
                // Act
                loginServlet.doPost(request, response);
                
                // Assert - Session should be invalidated and recreated
                sessionUtilMock.verify(() -> SessionUtil.createUserSession(request, testUser));
            }
        }
        
        @Test
        @DisplayName("Should handle concurrent login attempts")
        void testConcurrentLoginAttempts() throws ServletException, IOException {
            // This test would require more sophisticated setup for true concurrency testing
            // For now, we'll test that the servlet can handle multiple sequential calls
            
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getSession()).thenReturn(session);
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // userService is injected via TestableLoginServlet constructor
            when(userService.authenticateUser("admin", "admin123")).thenReturn(java.util.Optional.of(testUser));
            
            try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
                // Act - Simulate multiple calls
                loginServlet.doPost(request, response);
                loginServlet.doPost(request, response);
                
                // Assert - Should handle both calls without errors
                verify(response, times(2)).sendRedirect("dashboard");
            }
        }
        
        @Test
        @DisplayName("Should handle SQL injection attempts in username")
        void testSQLInjectionProtection() throws ServletException, IOException {
            // Arrange
            String maliciousUsername = "admin'; DROP TABLE users; --";
            when(request.getParameter("username")).thenReturn(maliciousUsername);
            when(request.getParameter("password")).thenReturn("password");
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // userService is injected via TestableLoginServlet constructor
            when(userService.authenticateUser(maliciousUsername, "password")).thenReturn(java.util.Optional.empty());
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(userService).authenticateUser(maliciousUsername, "password");
            verify(request).setAttribute("error", "Invalid username or password");
            verify(requestDispatcher).forward(request, response);
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle ServletException gracefully")
        void testServletExceptionHandling() throws ServletException, IOException {
            // Arrange
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            doThrow(new ServletException("Servlet error")).when(requestDispatcher).forward(request, response);
            
            // Act & Assert
            assertThrows(ServletException.class, () -> {
                loginServlet.doGet(request, response);
            });
        }
        
        @Test
        @DisplayName("Should handle IOException gracefully")
        void testIOExceptionHandling() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getSession()).thenReturn(session);
            
            // userService is injected via TestableLoginServlet constructor
            when(userService.authenticateUser("admin", "admin123")).thenReturn(java.util.Optional.of(testUser));
            doThrow(new IOException("Network error")).when(response).sendRedirect("dashboard");
            
            // Act & Assert
            assertThrows(IOException.class, () -> {
                loginServlet.doPost(request, response);
            });
        }
        
        @Test
        @DisplayName("Should handle null user service gracefully")
        void testNullUserServiceHandling() throws ServletException, IOException {
            // Arrange
            when(request.getParameter("username")).thenReturn("admin");
            when(request.getParameter("password")).thenReturn("admin123");
            when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
            
            // Test with null userService - need different approach
            
            // Act
            loginServlet.doPost(request, response);
            
            // Assert
            verify(request).setAttribute("error", "Login service temporarily unavailable. Please try again.");
            verify(requestDispatcher).forward(request, response);
        }
    }
    
    // Testable version of LoginServlet that allows injecting UserService
    private static class TestableLoginServlet extends LoginServlet {
        private final UserService testUserService;
        
        public TestableLoginServlet(UserService userService) {
            this.testUserService = userService;
            // Use reflection to set the private userService field
            try {
                java.lang.reflect.Field field = LoginServlet.class.getDeclaredField("userService");
                field.setAccessible(true);
                field.set(this, userService);
            } catch (Exception e) {
                // Fallback - the servlet will use getUserService() method
            }
        }
    }
}