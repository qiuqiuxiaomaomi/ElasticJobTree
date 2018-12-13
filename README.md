# ElasticJobTree
基于当当网的弹性job

###Quartz的缺点

![](https://i.imgur.com/pnpGusk.png)

<pre>
Quartz集群缺陷：
              1）时间规则更改不方便，需同步更改数据库时间规则描述。
              2）Quartz集群当节点不在同一台服务器上，因为时钟的可能不同步导致节点对其他节
                 点状态产生的影响。

           Quartz是Java事实上的任务标准，但Quartz关注点在于定时任务而非数据，并无一套根据
      数据处理而定制化的流程。虽然Quartz可以基于数据库实现作业的高可用，但缺少分布式并行执行
      作业的功能。
</pre>

###ElasticJob

![](https://i.imgur.com/6ambTsF.png)

<pre>
ElasticJob主要功能：

      1）定时任务：
                 基于成熟的定时作业框架Quartz cron表达式执行定时任务。
      2）作业注册中心：
                 基于Zookeeper和其客户端Curator实现的全局作业注册控制中心，用于注册，控制
              ，协调分布式作业执行。
      3）作业分片：
                 将一个任务分片成多个小任务项在服务器上同时执行。
      5）弹性扩容伸缩
                 支持OneOff,Perpetual和SequencePerpetual三种作业模式。
      6）失效转移
                 运行中的作业服务器崩溃不会导致重新分片，只会在下次作业启动时分片，启用失效
            转移功能可以在本次作业过程中，监测其他作业服务器空闲，抓取未完成的孤儿分片项执行。
      7）运行时状态收集：
                 监控作业运行时状态，统计最近一段时间处理的数据成功和失败数量，记录作业上次
            运行开始时间，结束时间和下次运行时间。
      8）作业停止，恢复和禁用
                 用于操作作业启停，并可以禁止某作业运行。
      9）被错过执行的作业重新触发
                 自动记录错过执行的作业，并在上次作业完成后自动触发，
      10）多线程快速处理数据
                 使用多线程处理抓取到的数据，提升吞吐量。
      11）幂等性
                 重复作业任务项判定，不重复执行已运行的作业任务项。由于开启幂等性需要监听作业
             运行状态，对瞬时反复运行的作业队性能有较大影响。
      12）容错处理
                 作业服务器与Zookeeper服务器通信失败则立即停止运行，防止作业注册中心将失效
             的分片项分配给其他作业服务器，而当前作业服务器仍在执行任务，导致重复执行。
      13）Spring支持
      15）提供运维界面，可以管理作业和注册中心
</pre>

<pre>
Elastic Job

      Elastic底层的任务调度还是使用的quartz，通过zookeeper来动态给job节点分片。

      使用elastic-job开发的作业都是zookeeper的客户端，比如我希望3台机器跑job，我们将任务
      分成3片，框架通过zk的协调，最终会让3台机器分别分配到0,1,2的任务片，比如server0-->0，server1-->1，server2-->2，当server0执行时，可以只查询id%3==0的用户，server1执行时，只查询id%3==1的用户，server2执行时，只查询id%3==2的用户。

      任务部署多节点引发重复执行

          在上面的基础上，我们再增加server3，此时，server3分不到任务分片，因为只有3片，已
      经分完了。没有分到任务分片的作业程序将不执行。
 
          如果此时server2挂了，那么server2的分片项会分配给server3，server3有了分片，就会
      替代server2执行。
 
          如果此时server3也挂了，只剩下server0和server1了，框架也会自动把server3的分片随
      机分配给server0或者server1，可能会这样，server0-->0，server1-->1,2。

          这种特性称之为弹性扩容，即elastic-job名称的由来
</pre>

![](https://i.imgur.com/l3r2EmO.png)

<pre>
任务的配置：

    由于内部使用quartz作为任务调度框架，任务的配置的相关的基础信息也是和quartz一致
    的。elastic-job的任务配置类在quartz的基础上（执行方法，cron表达式等）额外封装了
    分片策略，监控作业相关参数以及与注册中心的时间误差秒数等配置项。
</pre>

<pre>
任务的注册：

        elastic-job是通过zookeeper进行任务协调和故障转移的，任务的注册也就是把任务注
    册到zookeeper里面去。任务的注册包含在任务的启动过程中。根节点是项目的名称，下面一级是
    任务的名称。任务一旦创建则不能修改任务的名称，如果修改名称将视为新的任务，创建新的节
    点。任务名称节点下又包含5个数据子节点，分别是config, instances, leader, servers
    和sharding

        1. config节点：

           任务的配置信息，包含执行类，cron表达式，分片算法类，分片数量，分片参数等
        等。config节点的数据是通过ConfigService持久化到zookeeper中去的。默认状态下，
        如果你修改了Job的配置比如cron表达式，分片数量等是不会更新到zookeeper上去的，
        除非你把参数overwrite修改成true。

        2. instances节点：

           同一个Job下的elastic-job的部署实例。一台机器上可以启动多个Job实例，也就是
        Jar包。instances的命名是IP+@-@+PID。

        3. leader节点：

           任务实例的主节点信息，通过zookeeper的主节点选举，选出来的主节点信息。下面
        的子节点分为election，sharding和failover三个子节点。分别用于主节点选举，分片
        和失效转移处理。election下面的instance节点显式了当前主节点的
        实例ID：jobInstanceId。latch节点也是一个永久节点用于选举时候的实现分布式
        锁。sharding节点下面有一个临时节点，necessary，是否需要重新分片的标记。如果分
        片总数变化，或任务实例节点上下线或启用/禁用，以及主节点选举，都会触发设置重分片
        标记，主节点会进行分片计算。

        4. servers节点：

           任务实例的信息，主要是IP地址，任务实例的IP地址。如果多个任务实例在同一台机器
       上运行则只会出现一个IP子节点。可在IP地址节点写入DISABLED表示该任务实例禁用。 在新
       的cloud native架构下，servers节点大幅弱化，仅包含控制服务器是否可以禁用这一功
       能。为了更加纯粹的实现job核心，servers功能未来可能删除，控制服务器是否禁用的能力
       应该下放至自动化部署系统。

       5. sharding节点：

          任务的分片信息，子节点是分片项序号，从零开始，至分片总数减一。分片个个数是在任
       务配置中设置的。分片项序号的子节点存储详细信息。每个分片项下的子节点用于控制和记录
       分片运行状态。最主要的子节点就是instance。举例来说，上图有三个分片，每个分片下
       面有个instance的节点，也就说明了这个分片在哪个instance上运行。如上文所说如果分
       片总数变化，或任务实例节点上下线或启用/禁用，以及主节点选举，都会触发设置重分片
       标记，主节点会进行分片计算。分片计算的结果也就体现在这instance上。
</pre>

<pre>
Zookeeper分片

      业务迅速发展带来了跑批数据量的急剧增加。单机处理跑批数据已不能满足需要，另考虑到企业处
      理数据的扩展能力，多机跑批势在必行。多机跑批是指将跑批任务分发到多台服务器上执行，多机
      跑批的前提是”数据分片”。elasticJob通过JobShardingStrategy支持分片跑批。

      ElasticJob 默认提供了如下三种分片策略：
             1）AverageAllocationJobShardingStrategy:
                基于平均算法的分片策略；
             2）OdevitySortByNameJobShardingStrategy:
                根据作业名的哈希值奇偶数决定IP升降序算法的分片策略
             3）RotateServerByNameJobShardingStrategy:
                根据作业名的哈希值对服务器列表进行轮转的分片策略
         
             默认使用AverageAllocationJobShardingStrategy分片策略。

      数据库曾片的分片方案：

             
</pre>