package app.config;

import app.exceptions.ApiException;
import app.security.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static Javalin app;

    public static Javalin startServer(int port, EntityManagerFactory emf) {
        //ServiceRegistry services = new ServiceRegistry(emf);
        RoutesRegistry routes = new RoutesRegistry(emf);
        
        app = Javalin.create(config -> configure(config, routes));
        // Dette gør at rollen bliver godkendt og gennemgået FØR endpointet bliver iværksat
        SecurityController securityController = new SecurityController();
        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());


        setCORS();
        setSecurity();
        setGeneralExceptionHandling();
        setDebugHeaderLogging();

        app.start(port);
        return app;
    }

    /*
    // Method for starting test server so roles are bypassed
    public static Javalin startServerForTest(int port, EntityManagerFactory emf) {
        ServiceRegistry services = new ServiceRegistry(emf);
        RoutesRegistry routes = new RoutesRegistry(services);

        app = Javalin.create(config -> configure(config, routes));

        // app.beforeMatched(securityController.authenticate());
        // app.beforeMatched(securityController.authorize())

        setCORS();
        //setSecurity();
        setGeneralExceptionHandling();
        setDebugHeaderLogging();

        app.start(port);
        return app;
    }
     */

    private static void configure(JavalinConfig config, RoutesRegistry routes) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableDevLogging();
        config.bundledPlugins.enableRouteOverview("/routes");
        config.staticFiles.add("/public");
        config.http.defaultContentType = "application/json";
        config.router.contextPath = "/api/v1";

        // TODO: ligger i RoutesRegistry
        config.router.apiBuilder(routes.getRoutes());
    }

    private static void setCORS() {
        app.before(ApplicationConfig::setCorsHeaders);
        app.options("/*", ApplicationConfig::setCorsHeaders);
    }

    private static void setCorsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    private static void setSecurity() {
        SecurityController securityController = new SecurityController();
        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());
    }

    private static void setGeneralExceptionHandling() {
        app.exception(Exception.class, (e, ctx) -> {
            int statusCode = (e instanceof ApiException apiEx) ? apiEx.getStatusCode() : 500;
            String message = (e instanceof ApiException) ? e.getMessage() : "Internal server error";

            logger.error("An exception occurred", e);

            ObjectNode on = jsonMapper.createObjectNode()
                    .put("status", statusCode)
                    .put("msg", message);

            ctx.json(on);
            ctx.status(statusCode);
        });
    }

    private static void setDebugHeaderLogging() {
        app.before(ctx -> {
            String pathInfo = ctx.req().getPathInfo();
            System.out.println("Request path: " + pathInfo);
            ctx.req().getHeaderNames().asIterator().forEachRemaining(header ->
                    System.out.println("Header: " + header + " = " + ctx.req().getHeader(header)));
        });
    }

    public static void stopServer() {
        if (app != null) {
            System.out.println("Stopping server and closing EMF...");
            app.stop();
            if (HibernateConfig.getEntityManagerFactory().isOpen()) {
                HibernateConfig.getEntityManagerFactory().close();
            }
        }
    }
}
