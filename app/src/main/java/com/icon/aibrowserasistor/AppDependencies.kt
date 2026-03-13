package com.icon.aibrowserasistor

import com.icon.aibrowserasistor.agent.AgentMemory
import com.icon.aibrowserasistor.agent.AgentClickTool
import com.icon.aibrowserasistor.agent.AgentExtractTextTool
import com.icon.aibrowserasistor.agent.AgentOpenUrlTool
import com.icon.aibrowserasistor.agent.AgentTypeTool
import com.icon.aibrowserasistor.agent.DefaultAgentExecutor
import com.icon.aibrowserasistor.agent.RuleBasedPlanner
import com.icon.aibrowserasistor.agent.ToolExecutor
import com.icon.aibrowserasistor.agent.ToolRegistry
import com.icon.aibrowserasistor.ai.sdk.AIClient
import com.icon.aibrowserasistor.ai.sdk.FakeAIClient
import com.icon.aibrowserasistor.ai.sdk.PromptManager
import com.icon.aibrowserasistor.browser.BrowserController
import com.icon.aibrowserasistor.browser.DefaultBrowserController
import com.icon.aibrowserasistor.data.session.SessionRepository
import com.icon.aibrowserasistor.data.vector.InMemoryVectorStore
import com.icon.aibrowserasistor.data.vector.VectorStore
import com.icon.aibrowserasistor.parser.ContentParser
import com.icon.aibrowserasistor.parser.DefaultContentParser
import com.icon.aibrowserasistor.rag.RAGEngine
import com.icon.aibrowserasistor.rag.FakeEmbeddingService
import com.icon.aibrowserasistor.tools.web.WebToolExecutor
import com.icon.aibrowserasistor.tools.web.WebToolRegistry

class AppDependencies {
    private val vectorStore: VectorStore = InMemoryVectorStore()
    private val contentParser: ContentParser = DefaultContentParser()
    private val aiClient: AIClient = FakeAIClient()
    private val promptManager = PromptManager()
    private val sessionRepository = SessionRepository()
    private val agentMemory = AgentMemory()
    private val embeddingService = FakeEmbeddingService()

    private val webToolRegistry = WebToolRegistry(
        listOf(
            com.icon.aibrowserasistor.tools.web.OpenUrlTool(),
            com.icon.aibrowserasistor.tools.web.ClickTool(),
            com.icon.aibrowserasistor.tools.web.TypeTool(),
            com.icon.aibrowserasistor.tools.web.ExtractTextTool()
        )
    )
    private val webToolExecutor = WebToolExecutor(webToolRegistry)
    private val agentToolRegistry = ToolRegistry(
        listOf(
            AgentOpenUrlTool(webToolExecutor),
            AgentClickTool(webToolExecutor),
            AgentTypeTool(webToolExecutor),
            AgentExtractTextTool(webToolExecutor)
        )
    )
    private val agentToolExecutor = ToolExecutor(agentToolRegistry)
    private val ragEngine: RAGEngine = RAGEngine(embeddingService, vectorStore)
    private val agentPlanner = RuleBasedPlanner()

    val browserController: BrowserController = DefaultBrowserController(webToolExecutor)
    val agentExecutor = DefaultAgentExecutor(ragEngine, agentPlanner, agentToolExecutor, agentMemory)

    fun parser(): ContentParser = contentParser

    fun ai(): AIClient = aiClient

    fun prompt(): PromptManager = promptManager

    fun session(): SessionRepository = sessionRepository

    fun memory(): AgentMemory = agentMemory

    fun rag(): RAGEngine = ragEngine
}
