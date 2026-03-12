package com.icon.aibrowserasistor.agent

import com.icon.aibrowserasistor.common.AgentTask
import com.icon.aibrowserasistor.common.AiResponseChunk
import com.icon.aibrowserasistor.rag.RAGEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface Tool {
    val name: String
    suspend fun execute(params: Map<String, Any>): String
}

data class ToolCall(val tool: String, val params: Map<String, Any> = emptyMap())

class ToolRegistry(tools: List<Tool>) {
    private val registry = tools.associateBy { it.name }

    fun get(name: String): Tool? = registry[name]
}

class ToolExecutor(private val registry: ToolRegistry) {
    suspend fun execute(call: ToolCall): String {
        val tool = registry.get(call.tool) ?: return "Tool ${call.tool} not found"
        return tool.execute(call.params)
    }
}

class AgentMemory {
    private val records = mutableListOf<String>()

    fun remember(entry: String) {
        records.add(entry)
    }

    fun history(): List<String> = records.toList()
}

interface Planner {
    suspend fun plan(task: AgentTask): List<ToolCall>
}

class RuleBasedPlanner : Planner {
    override suspend fun plan(task: AgentTask): List<ToolCall> {
        val steps = mutableListOf<ToolCall>()
        if (!task.contextUrl.isNullOrBlank()) {
            steps.add(ToolCall(tool = "open_url", params = mapOf("url" to task.contextUrl)))
        }
        steps.add(ToolCall(tool = "extract_text"))
        if (task.goal.contains("click", ignoreCase = true)) {
            steps.add(ToolCall(tool = "click", params = mapOf("selector" to task.goal)))
        }
        return steps
    }
}

interface AgentExecutor {
    fun run(task: AgentTask): Flow<AiResponseChunk>
}

class DefaultAgentExecutor(
    private val ragEngine: RAGEngine,
    private val planner: Planner,
    private val toolExecutor: ToolExecutor,
    private val memory: AgentMemory
) : AgentExecutor {

    override fun run(task: AgentTask): Flow<AiResponseChunk> = flow {
        emit(AiResponseChunk(text = "Planning for: ${task.goal}", isFinal = false))
        val plan = planner.plan(task)
        memory.remember("plan:${plan.joinToString { it.tool }}")

        val context = ragEngine.retrieveContext(task.goal)
        memory.remember("context:${context.size}")
        emit(AiResponseChunk(text = "Context retrieved: ${context.size} chunks", isFinal = false))

        for (call in plan) {
            emit(AiResponseChunk(text = "Running tool ${call.tool}", isFinal = false))
            val result = toolExecutor.execute(call)
            memory.remember("tool:${call.tool}:$result")
            emit(AiResponseChunk(text = result, isFinal = false))
        }

        emit(AiResponseChunk(text = "Agent finished", isFinal = true))
    }
}
