package com.canthny.drools_kie_demo.test;

import com.alibaba.fastjson.JSONObject;
import com.canthny.drools_kie_demo.domain.Assets;
import com.canthny.drools_kie_demo.domain.UserInfo;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import java.util.LinkedList;
import java.util.List;

/**
 * Description： kie-server动态部署demo测试
 * Created By tanghao on 2019/7/2
 */
public class DroolKieTestDemo {

    public static void main(String[] args) {
        KieServicesConfiguration kieServicesConfiguration = KieServicesFactory.newRestConfiguration("http://192.168.8.132:8080/kie-server/services/rest/server","kieserver","kieserver1!");
        kieServicesConfiguration.setMarshallingFormat(MarshallingFormat.JSON);
        // 创建规则服务客户端
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(kieServicesConfiguration);
        RuleServicesClient ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setName("canthny");
        userInfo.setAge(31);
        Assets assets = new Assets();
        assets.setMoney(20L);
        assets.setFund(30L);
        assets.setStock(50L);
        userInfo.setAssets(assets);

        // 命令定义，包含插入数据，执行规则
        KieCommands kieCommands = KieServices.Factory.get().getCommands();
        List<Command<?>> commands = new LinkedList<>();
        commands.add(kieCommands.newInsert(userInfo, "userInfo"));
        commands.add(kieCommands.newFireAllRules());
        ServiceResponse<ExecutionResults> results = ruleServicesClient.executeCommandsWithResults("drools-kie-demo_1.0.0",
                kieCommands.newBatchExecution(commands, "session1"));

        // 返回值读取
        UserInfo value = (UserInfo) results.getResult().getValue("userInfo");
        System.out.println(JSONObject.toJSON(value).toString());
    }
}
