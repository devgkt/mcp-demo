package com.gkt.demoMcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpTransport;

import java.time.Duration;
import java.util.List;
import java.util.Map;


public class MyClientService {
    public static void main(String[] args) {

        //Configure Server
        ServerParameters params = ServerParameters.builder("/path_to_sqlcl/sqlcl/bin/sql")
                .args("-mcp")
                .build();
        McpTransport transport = new StdioClientTransport(params);

        //Configure Client
        McpSyncClient client = McpClient.sync((McpClientTransport) transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(McpSchema.ClientCapabilities.builder()
                        .roots(true)      // Enable roots capability
                        .sampling()       // Enable sampling capability
                        .build())
                .build();

        // Initialize connection with Server
        client.initialize();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // List available tools
        System.out.println("List available tools and wait 1 seconds");
        McpSchema.ListToolsResult tools = client.listTools();
        tools.tools().stream().forEach(tool -> {
            System.out.println(tool.name());
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Call a tool
        /*System.out.println("Calling connect tool... and wait 2 seconds");
        McpSchema.CallToolResult connResult = client.callTool(new McpSchema.CallToolRequest("connect", Map.of(
                "connection_name", "devconn",
                "mcp_client", "UNKNOWN-MCP-CLIENT",
                "model", "UNKNOWN-LLM"
        )));
        System.out.println(connResult.toString());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */

        System.out.println("Running tool: "+"connect");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        McpSchema.CallToolResult result1 = client.callTool(new McpSchema.CallToolRequest("connect", Map.of(
                //"sqlcl", "GET /Users/germanrodriguez/Documents/my_command.sql",
                "connection_name", "localconn1",
                "mcp_client", "UNKNOWN-MCP-CLIENT",
                "model", "UNKNOWN-LLM"
        )));

        // Call a tool
        String cmd ="awr create html";
        System.out.println("Running sqlcl command: "+cmd);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        McpSchema.CallToolResult result = client.callTool(new McpSchema.CallToolRequest("run-sqlcl", Map.of(
                //"sqlcl", "GET /Users/germanrodriguez/Documents/my_command.sql",
                "sqlcl", cmd,
                "mcp_client", "UNKNOWN-MCP-CLIENT",
                "model", "UNKNOWN-LLM"
        )));
        List<McpSchema.Content> list = result.content();

        list.stream().forEach(content -> {
            McpSchema.TextContent e = (McpSchema.TextContent) content;
            System.out.println(e.text());
        });
//        McpSchema.TextContent e = (McpSchema.TextContent) list.get(0);
//        System.out.println(e.text());
        System.out.println("Will try to close the client after 1 seconds");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        boolean isClosed = client.closeGracefully();
        if (!isClosed) {
            System.out.println("Closing connection failed");
        }else{
            System.out.println("Closing connection worked");
        }
    }
}
