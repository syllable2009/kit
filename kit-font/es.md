ECMAScript (简称 ES) 是由 Ecma 国际组织制定的‌脚本语言标准规范‌.
JavaScript (JS)，浏览器原生支持的脚本语言。
TypeScript (TS)，JS 的超集，需编译为 JS 后运行。
Vue是一款用于构建用户界面的 JavaScript 框架。它基于标准 HTML、CSS 和 JavaScript 构建，并提供了一套声明式的、组件化的编程模型，帮助你高效地开发用户界面。无论是简单还是复杂的界面，Vue 都可以胜任。
Vite（法语意为 "快速的"，发音 /vit/，发音同 "veet"）是一种新型前端构建工具，能够显著提升前端开发体验
Element Plus，基于 Vue 3，面向设计师和开发者的组件库。
Pinia 是 Vue 的专属状态管理库，它允许你跨组件或页面共享状态。
Tailwindcss只需书写 HTML 代码，无需书写 CSS，即可快速构建美观的网站。
本 CSS 框架本质上是一个工具集，包含了大量类似 flex、 pt-4、 text-center 以及 rotate-90 等工具类，可以组合使用并直接在 HTML 代码上实现任何 UI 设计。
Node.js 开发环境为 JavaScript 提供了‌服务器端运行能力‌，使开发者能够突破浏览器限制，构建跨平台的后端服务、工具链及高性能应用6
npm‌（Node Package Manager）是 Node.js 的默认包管理工具，同时也是全球最大的 JavaScript 代码模块生态系统.

# https://vue3.chengpeiquan.com/guide.html

初始化一个node项目 cd project, npm init -y
package.json,这是 Node 项目的清单，里面记录了这个项目的基础信息、依赖信息、开发过程的脚本行为、发布相关的信息等等，未来将在很多项目里看到它的身影。
在实际的项目中，往往需要填写更完善的项目信息，除了手动维护这些信息之外，在安装 npm 包等操作时， Node 也会帮写入数据到这个文件里.
字段名	含义
name	项目名称，如果打算发布成 npm 包，它将作为包的名称
version	项目版本号，如果打算发布成 npm 包，这个字段是必须的，遵循 语义化版本号 的要求
description	项目的描述
keywords	关键词，用于在 npm 网站上进行搜索
homepage	项目的官网 URL
main	项目的入口文件
scripts	指定运行脚本的命令缩写，常见的如 npm run build 等命令就在这里配置，详见 脚本命令的配置
author	作者信息
license	许可证信息，可以选择适当的许可证进行开源
dependencies	记录当前项目的生产依赖，安装 npm 包时会自动生成，详见：依赖包和插件
devDependencies	记录当前项目的开发依赖，安装 npm 包时会自动生成，详见：依赖包和插件
type	配置 Node 对 CJS 和 ESM 的支持




# 前端工程化
做一个页面，是先创建 HTML 页面文件写页面结构，再在里面写 CSS 代码美化页面，再根据需要写一些 JavaScript 代码增加交互功能。
而实际上的前端开发工作，早已进入了前端工程化开发的时代，已经充满了各种现代化框架、预处理器、代码编译…
最终的产物也不再单纯是多个 HTML 页面，经常能看到 SPA / SSR / SSG 等词汇的身影。


npm run test
这等价于直接在命令行执行 node index.js 命令，其中 node 是 Node.js 运行文件的命令， index 是文件名，相当于 index.js ，因为 JS 文件名后缀可以省略。

模块化与包
ESM （ ES Module ） 是 JavaScript 在 ES6（ ECMAScript 2015 ）版本推出的模块化标准，旨在成为浏览器和服务端通用的模块解决方案。
CJS （ CommonJS ） 原本是服务端的模块化标准（设计之初也叫 ServerJS ），是为 JavaScript 设计的用于浏览器之外的一个模块化方案， Node 默认支持了该规范，在 Node 12 之前也只支持 CJS ，但从 Node 12 开始，已经同时支持 ES Module 的使用。
至此，不论是 Node 端还是浏览器端， ES Module 是统一的模块化标准了。


hello-node
│ # 源码文件夹
├─src
│ │ # 业务文件夹
│ └─cjs
│   │ # 入口文件
│   ├─index.cjs
│   │ # 模块文件
│   └─module.cjs
│ # 项目清单
└─package.json

