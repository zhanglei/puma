package com.dianping.puma.admin.web;

import com.dianping.puma.admin.util.GsonUtil;
import com.dianping.puma.core.entity.PumaTask;
import com.dianping.puma.core.entity.SrcDBInstance;
import com.dianping.puma.core.service.PumaTaskService;
import com.dianping.puma.core.service.SrcDBInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SrcDBInstanceController {

	private static final Logger LOG = LoggerFactory.getLogger(SrcDBInstanceController.class);

	@Autowired
	SrcDBInstanceService srcDBInstanceService;

	@Autowired
	PumaTaskService pumaTaskService;

	@RequestMapping(value = { "/src-db-instance" })
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<SrcDBInstance> srcDBInstanceEntities = srcDBInstanceService.findAll();

		map.put("entities", srcDBInstanceEntities);
		map.put("path", "src-db-instance");
		return new ModelAndView("main/container", map);
	}

	@RequestMapping(value = { "/src-db-instance/create" })
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", "src-db-instance");
		map.put("subPath", "create");
		return new ModelAndView("main/container", map);
	}

	@RequestMapping(value = { "/src-db-instance/update" })
	public ModelAndView update(String id) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<PumaTask> pumaTasks = pumaTaskService.findBySrcDBInstanceId(id);
			if (pumaTasks != null && pumaTasks.size() != 0) {
				map.put("lock", true);
			} else {
				map.put("lock", false);
			}

			SrcDBInstance entity = srcDBInstanceService.find(id);
			map.put("entity", entity);
			map.put("path", "src-db-instance");
			map.put("subPath", "create");

		} catch (Exception e) {
			// @TODO: error page.
		}

		return new ModelAndView("main/container", map);
	}

	@RequestMapping(value = { "/src-db-instance/create" }, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String createPost(
			String id,
			String name,
			Integer serverId,
			String host,
			Integer port,
			String username,
			String password,
			String metaHost,
			Integer metaPort,
			String metaUsername,
			String metaPassword) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			SrcDBInstance srcDbInstance;

			if (id != null) {
				srcDbInstance = srcDBInstanceService.find(id);
			} else {
				srcDbInstance = new SrcDBInstance();
			}

			srcDbInstance.setName(name);
			srcDbInstance.setServerId(serverId);
			srcDbInstance.setHost(host);
			srcDbInstance.setPort(port);
			srcDbInstance.setUsername(username);
			srcDbInstance.setPassword(password);
			srcDbInstance.setMetaHost(metaHost);
			srcDbInstance.setMetaPort(metaPort);
			srcDbInstance.setMetaUsername(metaUsername);
			srcDbInstance.setMetaPassword(metaPassword);

			if (id != null) {
				srcDBInstanceService.update(srcDbInstance);
			} else {
				srcDBInstanceService.create(srcDbInstance);
			}

			map.put("success", true);
		} catch (Exception e) {
			map.put("err", e.getMessage());
			map.put("success", false);
		}

		return GsonUtil.toJson(map);
	}

	@RequestMapping(value = { "/src-db-instance/remove" }, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String removePost(String id) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<PumaTask> pumaTasks = pumaTaskService.findBySrcDBInstanceId(id);
			if (pumaTasks != null && pumaTasks.size() != 0) {
				map.put("lock", true);
				map.put("success", false);
			} else {
				this.srcDBInstanceService.remove(id);
				map.put("lock", false);
				map.put("success", true);
			}
		} catch (Exception e) {
			map.put("success", false);
		}

		return GsonUtil.toJson(map);
	}
}