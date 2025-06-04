package server

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info

object SwaggerDocService extends SwaggerHttpService {
  override val apiClasses = Set(classOf[Routes])
  override val host       = "localhost:8443"
  override val info       = Info(version = "1.0", title = "Webhook API")
}
