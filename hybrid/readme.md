前端 H5 现在可以通过如下方式调用 Native：
```
function callNative(plugin, method, params, callbackId) {
    const message = JSON.stringify({
        id: callbackId,
        method: `${plugin}.${method}`,
        params: params
    });
    const url = "jsbridge://postMessage?data=" + encodeURIComponent(message);
    
    // 触发拦截
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = url;
    document.documentElement.appendChild(iframe);
    setTimeout(() => document.documentElement.removeChild(iframe), 0);
}
```

```
    // 在 Compose 中使用
    HybridView(
        url = "https://static.example.com/index.html",
        modifier = Modifier.fillMaxSize()
    )
    
    // 初始化离线包更新 (建议在 Application 或首屏)
    val resourceManager = ResourceManager(context)
    resourceManager.checkUpdate()
    
```

