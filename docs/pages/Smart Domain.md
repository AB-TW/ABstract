- [Smart Domain](https://github.com/Re-engineering-Domain-Driven-Design/Accounting/blob/main/public/Smart%20Domain%20Pattern.pdf?raw=true)是一种新的软件架构模式，主要用于实现领域驱动设计（DDD）。相较于传统的分层架构，Smart Domain更加注重从业务领域的角度去划分层次，并在每一层次上都使用领域专家和开发人员共同理解的通用语言进行交流，从而建立起完整的领域模型。与此同时，Smart Domain还摒弃了服务层的概念，将原本在服务层实现的逻辑下沉到领域模型中，从而让领域模型更加丰满和清晰。
- 总之，Smart Domain是一种新的软件架构模式，通过从业务领域的角度去划分层次和使用通用语言建立领域模型，以及将服务层逻辑下沉到领域模型中，提高了领域驱动设计的实现效果。
- [](https://github.com/Re-engineering-Domain-Driven-Design/Accounting)[https://github.com/Re-engineering-Domain-Driven-Design/Accounting](https://github.com/Re-engineering-Domain-Driven-Design/Accounting)
- [直播加餐02｜如何使用Smart Domain实现DDD？-极客时间](https://time.geekbang.org/column/article/556440)
-
-
## SmartDomain 分层

Smart Domain 架构模式，将系统架构分为三层：

1. Domain层
Domain层是整个系统的核心，主要定义业务相关的实体（Entity）和表示实体之间关联关系的关联对象。该层定义的实体和关联对象是业务模型的核心，反映了整个系统的业务规则和逻辑。同时，该层仅提供接口或抽象类定义，不包含具体实现。这样做可以避免业务逻辑和实现细节的混淆，也方便后续的扩展和维护。
2. 集成层
集成层主要负责Domain层中关系对象的实现，该层会依赖数据库、Http接口访问进行功能实现。该层的职责是将Domain层的抽象定义转换成具体实现，包括数据持久化、网络通信等。该层还可以处理一些跨域访问和数据加密解密等底层功能。
3. API层
API层是整个系统的入口，基于Domain层的Entity或关联对象提供的能力，提供外部访问的RestFul接口。该层负责处理外部请求，将请求参数转换成业务对象并调用Domain层的业务逻辑实现，返回结果给客户端。该层还可以对请求参数进行验证、安全过滤等操作，保证系统的安全性和可靠性。
-
- {{embed [[SmartDomain GPT Prompt]]}}
-
-