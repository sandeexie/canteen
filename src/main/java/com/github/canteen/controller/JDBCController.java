package com.github.canteen.controller;

import com.github.canteen.handler.*;
import com.github.canteen.internal.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO 替换掉Spring系列对HTTP C/S模式的支持,这里支持HTTP请求即可,暂时不考虑HTTPS
 */
@RestController
@Controller
public class JDBCController {

	private static final ConfigReader configReader=ConfigReader.getReader();

	private static final Logger logger= LoggerFactory.getLogger(JDBCController.class);

	@Autowired
	MySQLHandler mySQLHandler;

	@Autowired
	OracleHandler oracleHandler;

	@Autowired
	PostgreHandler postgreHandler;

	@Autowired
	HQLHandler hqlHandler;

	@Autowired
	SparkSQLHandler sparkSQLHandler;

	@Autowired
	FlinkSQLHandler flinkSQLHandler;

	@RequestMapping("/sql/mysql/")
	public @ResponseBody Object requestMySQL(
			@RequestParam String username,
			@RequestParam String url,
			@RequestParam String sql){

		return null;
	}

	@RequestMapping("/sql/oracle")
	public @ResponseBody Object requestOracle(
			@RequestParam String username,
			@RequestParam String url,
			@RequestParam String sql){
		return null;
	}

	@RequestMapping("/sql/postgre")
	public @ResponseBody Object requestPostgre(
			@RequestParam String username,
			@RequestParam String url,
			@RequestParam String sql){
		return null;
	}

	@RequestMapping("/sql/hivesql")
	public @ResponseBody Object requestHiveSQL(
			@RequestParam String username,
			@RequestParam String url,
			@RequestParam String sql){
		return null;
	}

	@RequestMapping("/sql/sparkSQL")
	public @ResponseBody Object requestSparkSQL(
			@RequestParam String username,
			@RequestParam String url,
			@RequestParam String sql){
		return null;
	}

	@RequestMapping("/sql/FlinkSQL")
	public @ResponseBody Object requestFlinkSQL(
			@RequestParam String username,
			@RequestParam String url,
			@RequestParam String sql){
		return null;
	}

}
