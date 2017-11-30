package load_balancing

object ClusterLoadBalanceApp extends App {
  //initiate three nodes from backend
  Backend.initiate(2551)

  Backend.initiate(2552)

  Backend.initiate(2561)

  //initiate frontend node
  Frontend.initiate()
}
