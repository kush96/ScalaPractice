import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object FuturesOptionsHandling extends App {
  case class Job(jobId: String, title: String, clicks: Option[Int] = None, applies: Option[Int] = None)
  case class ClicksStat(jobId: String, clicks: Int)
  case class AppliesStat(jobId: String, applies: Int)
  val jobs = Future.successful(List(Job("job1", "title1"), Job("job2", "title1"), Job("job3", "title3")))
  val clicks = Future.successful(List(ClicksStat("job2", 50)))
  val applies = List(AppliesStat("job3", 150))
  // If stats are not present clicks/Applies should be None
  val jobsEnrichedWithStats = for {
    jobList <- jobs
    clicksList <- clicks
    jobListWithClicks = jobList.flatMap(job => clicksList.map(click => if (click.jobId == job.jobId) Job(job.jobId, job.title, Some(click.clicks), None) else Job(job.jobId, job.title, None, None)))
    jobListWithClicksAndApplies = jobListWithClicks.flatMap(jobWithClicks => applies.map(apply => if (apply.jobId == jobWithClicks.jobId) Job(jobWithClicks.jobId, jobWithClicks.title, jobWithClicks.clicks, Some(apply.applies)) else Job(jobWithClicks.jobId, jobWithClicks.title, jobWithClicks.clicks, None)))
  } yield (jobListWithClicksAndApplies) // It should contain => Future(List(Job("job1", "title1"), Job("job2", "title1", Some(50), None), Job("job3", "title3", None, Some(150))))
  Thread.sleep(1000)
  println(jobsEnrichedWithStats)
  val jobsWithClicksNotNone = Await.result(jobsEnrichedWithStats.map(jobsWithStatList => jobsWithStatList.filter(_.clicks.isDefined)), 2 seconds)
  println(jobsWithClicksNotNone)
  val jobsWithAppliesNotNone = Await.result(jobsEnrichedWithStats.map(jobsWithStatsList => jobsWithStatsList.filter(_.applies.isDefined)), 2 seconds)
  println(jobsWithAppliesNotNone)
  //  title -> List[Job]
  def makeMap(jobList: List[Job]): Map[String, List[Job]] = {
    val v: Map[String, List[Job]] = jobList.groupBy(_.title)
    v
  }
  val jobsGroupedByTitle = makeMap(Await.result(jobsEnrichedWithStats, 2 seconds))
  println(jobsGroupedByTitle)
  def makeMap2(x: List[Job]): Map[String, (Int, Int)] = {
    x.groupBy(_.title).map(title => (title._1, (title._2.flatMap(_.clicks).sum, title._2.flatMap(_.applies).sum)))
  }
  // Should return Map(title -> (sumClicks, sumApplies)). if clicks/applies is None, set its value as 0
  val statsPerTitle = makeMap2(Await.result(jobsEnrichedWithStats, 2 seconds))
  println(statsPerTitle)
}