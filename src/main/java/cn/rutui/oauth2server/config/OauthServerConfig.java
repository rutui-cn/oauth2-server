package cn.rutui.oauth2server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;


/**
 * 当前类为Oauth2 server的配置类（需要继承特定的父类 AuthorizationServerConfigurerAdapter）
 */
@Configuration
@EnableAuthorizationServer  // 开启认证服务器功能
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {


	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * 认证服务器最终是以api接口的方式对外提供服务（校验合法性并生成令牌、校验令牌等）
	 * 那么，以api接口方式对外的话，就涉及到接口的访问权限，我们需要在这里进行必要的配置
	 *
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		super.configure(security);
		// 相当于打开endpoints 访问接口的开关，这样的话后期我们能够访问该接口
		security
				// 允许客户端表单认证
				.allowFormAuthenticationForClients()
				// 开启端口/oauth/token_key的访问权限（允许）
				.tokenKeyAccess("permitAll()")
				// 开启端口/oauth/check_token的访问权限（允许）//"isAuthenticated()"
				.checkTokenAccess("permitAll()");
	}

	/**
	 * 客户端详情配置，
	 * 比如client_id，secret
	 * 当前这个服务就如同QQ平台，拉勾网作为客户端需要qq平台进行登录授权认证等，提前需要到QQ平台注册，QQ平台会给拉勾网
	 * 颁发client_id等必要参数，表明客户端是谁
	 *
	 * @param clients
	 * @throws Exception
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		super.configure(clients);

		// 从内存中加载客户端详情

		clients.inMemory()// 客户端信息存储在什么地方，可以在内存中，可以在数据库里
				.withClient("client_lagou")  // 添加一个client配置,指定其client_id
				.secret("abcxyz")                   // 指定客户端的密码/安全码
//				.resourceIds("")                // 指定客户端所能访问资源id清单，此处的资源id是需要在具体的资源服务器上也配置一样
				// 认证类型/令牌颁发模式，可以配置多个在这里，但是不一定都用，具体使用哪种方式颁发token，需要客户端调用的时候传递参数指定
				.authorizedGrantTypes("authorization_code", "password", "client_credentials")
				// 客户端的权限范围，此处配置为all全部即可
				.scopes("all");
	}

	/**
	 * 认证服务器是玩转token的，那么这里配置token令牌管理相关（token此时就是一个字符串，当下的token需要在服务器端存储，
	 * 那么存储在哪里呢？都是在这里配置）
	 *
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		super.configure(endpoints);
		endpoints
				.tokenStore(tokenStore())  // 指定token的存储方法
				.tokenServices(authorizationServerTokenServices())   // token服务的一个描述，可以认为是token生成细节的描述，比如有效时间多少等
				.authenticationManager(authenticationManager) // 指定认证管理器，随后注入一个到当前类使用即可
				.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
	}


	/*
		该方法用于创建tokenStore对象（令牌存储对象）
		token以什么形式存储
	 */
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}


	/**
	 * 该方法用户获取一个token服务对象（该对象描述了token有效期等信息）
	 */
	public AuthorizationServerTokenServices authorizationServerTokenServices() {
		// 使用默认实现
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setSupportRefreshToken(false); // 是否开启令牌刷新
		defaultTokenServices.setTokenStore(tokenStore());

		// 针对jwt令牌的添加
		defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());

		// 设置令牌有效时间（一般设置为2个小时）
		defaultTokenServices.setAccessTokenValiditySeconds(7200); // access_token就是我们请求资源需要携带的令牌
		// 设置刷新令牌的有效时间
		defaultTokenServices.setRefreshTokenValiditySeconds(259200); // 3天

		return defaultTokenServices;
	}

	private TokenEnhancer jwtAccessTokenConverter() {
		return (accessToken, authentication) -> accessToken;
	}
}