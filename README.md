# feima_dispatching
仿造蜂鸟配送app
感谢各种开源框架的协助

<h1>功能介绍：</h1><br/>
android 5.0 design设计风格 部分UI通过SVG转换成Vector(内存占有率少，目前适配也可以)<br />
框架采用MVP模式+ButterKnife视图注入+Dagger2对象注入模式<br />
编程风格采用RxJava风格 优雅的事件流风格 增加了阅读感+retrolambda风格 大大减少了代码量<br />
消息推送功能采用了两种模式 分别是RabbitMQ和Socket通讯 分别在两个分支中（坑啊，因为重做了，导致我这边也重做）<br />
网络通讯基于retrofit和okhttp 封装了一个工具类并且支持https加密 即拿即用 支持不同格式传输<br />
万能适配器功能 对话框等等帮助类 部分属性动画<br />
以及gradle的基本配置<br />
混淆文件的编辑<br />
高德地图 定位 搜索功能呢（导航功能暂时未做）
