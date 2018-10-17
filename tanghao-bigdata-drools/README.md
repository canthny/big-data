#drool规则引擎测试-手机缴费风险控制Demo
##1-单笔金额超限；
##2-手机号在黑名单中；
##3-充值金额不能大于近3天平均值的3倍

#启动服务
//mongodb启动命令中的ip跟着本机的动态ip改变
cd /usr/local/mongodb/bin
nohup ./mongod -bind_ip 192.168.0.102 &
//kafka的server.properties中advertised.listeners=PLAINTEXT://192.168.1.113:9092修改一下当前ip
cd /home/canthny/kafka_2.11-0.11.0.3
nohup bin/zookeeper-server-start.sh config/zookeeper.properties &
nohup bin/kafka-server-start.sh config/server.properties &
//redis启动本地windows的

//tanghao-bigdata-drools工程中配置mongodb和redis的ip为本地或虚拟机的ip
//tanghao-flink-demo工程中配置kafka的地址
#	数据准备
##1、	插入3天缴费数据十条，金额随机，执行TestMongodb中的testSave()方法插入数据
cd /usr/local/mongodb/bin
./mongo 192.168.0.102:27017/rule进入命令行查询数据
 
Mongodb常用命令
//进入库
use rule
//创建集合
db.createCollection("mobilePaymentInfo")
//查看所有集合
show collections
//查看当前库指定集合所有数据
db.mobilePaymentInfo.find()
//向当前库指定集合插入文档
db.mobilePaymentInfo.insert({"_class" : "com.tanghao.bigdata.drools.mongodb.domain.MobilePaymentInfo", "pay_order_no" : "2018092700001", "account_no" : "1234312412345234", "bank_card_no" : "62260113241234", "mobile" : "18709858763", "amount" : "12.00", "time" : "20180927 16:57:00" })
//删除文档下所有数据
db.mobilePaymentInfo.remove({})
//根据日期查询
db.mobilePaymentInfo.find({time:{'$gte':ISODate("2018-09-27"),'$lt':ISODate("2018-09-28")}})
##2、插入手机号黑名单数据，执行TestMongodb中的insertPhoneNoBlackList()方法
 
##3、计算3天内用户缴费平均金额，执行TestMongodb中的testMapReduce()方法，并将计算结果插入新表用户3日缴费平均金额中
 _id即为用户的account_no，value为3日缴费平均金额	


#单元测试
测试准备
	规则引擎测试：tanghao-bigdata-drools工程TestDrools单元测试中的testCase1\2\3\4方法
	规则执行顺序(pay_rule.drl文件中的salience属性)：单笔限额>手机号黑名单>充值平均值
testCase1()——单笔金额超限，打印风险，金额自行修改。
testCase2()——风控判断手机号是否在黑名单中，需要事先建立黑名单表数据，TestMongodb类insertPhoneNoBlackList中key插入黑名单数据，修改手机号运行单元测试即可。
testCase3()——判断当前单笔充值额度大于用户最近一个月充值额度平均值的3倍，需要事先进行MapReduce操作，产生一段时间内平均金额数据，插入中间表，在TestMongodb类testMapReduce方法中，自行调整日期参数即可，上节数据准备中第三步已经做过可忽略，也可以自行尝试。
testCase4()——一小时内支付次数或支付金额限制，需要先跑流计算测试，将数据插入缓存中，流程暂未串起来，后续可以写个消息生产者将流计算和规则引擎串起来

	流计算测试：tanghao-flink-demo工程GetPayInfoJob中main方法
//flink demo测试，kafka生产消息
cd /home/canthny/kafka_2.11-0.11.0.3
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic CountPerHourByAccountNo2

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic CountPerHourByAccountNo --from-beginning
{"accountId":"0716","amount":"130","eventTime":"2018/09/30 02:51:00"}
{"accountId":"0718","amount":"130","eventTime":"2018/09/30 02:51:01"}
{"accountId":"0718","amount":"130","eventTime":"2018/09/30 02:51:06"}
{"accountId":"0718","amount":"130","eventTime":"2018/09/30 02:51:07"}
{"accountId":"0716","amount":"140","eventTime":"2018/09/30 02:51:02"}
{"accountId":"0716","amount":"150","eventTime":"2018/09/30 02:51:06"}
{"accountId":"0716","amount":"150","eventTime":"2018/09/30 02:51:12"}

