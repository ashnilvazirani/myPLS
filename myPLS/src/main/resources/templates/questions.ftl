<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<title>Professor Home</title>

<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
	integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
	crossorigin="anonymous"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
	integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
	crossorigin="anonymous"></script>
<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
	integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
	crossorigin="anonymous"></script>

</head>
<body>
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
		<a class="navbar-brand" href="http://localhost:4567/professorDashboard">MyPLS</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup"
			aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarNavAltMarkup">
			<div class="navbar-nav mr-auto"></div>
			<div class="navbar-nav ml-auto">
				<a class="navbar-brand" href="http://localhost:4567/addQuestion">
					<button type="submit" class="btn btn-info" style="margin:5px;">Add Question</button>
				</a>
			</div>
		</div>
	</nav>
	<div class="jumbotron jumbotron-fluid">
		<div class="container">
			<h1 class="display-4">
				Welcome Professor!
			</h1>
			<#if courseID??>
				<p class="lead">Course: ${courseID}</p>
				<p class="lead">Quiz ID :</p>
			<#else>
				<p class="lead">Questions List</p>
			</#if>

		</div>
	</div>

	<div class="container">
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th scope="col">Problem</th>
					<th scope="col">Correct Answer</th>
				</tr>
			</thead>
			<tbody>
				<#list questions as q>
				<tr>
					<td scope="col">
					${q.getProblem()}
                    </td>
					<td scope="col">
					${q.getCorrectOption()}
                    </td>
                        <#if courseID??>
							<td>
							<#--  <a class="navbar-brand" href="http://localhost:4567/addQuestion/${courseID}/${q.questionID}">
								<button type="submit" class="btn btn-info" style="margin:5px;">Add Question</button>
							</a>  -->
                            <form class="form-inline" method="POST" action="/addQuestionToList">
                                <input name="courseID" value=${courseID} hidden/>
                                <input name="questionID" value=${q.questionID} hidden/>
                                <button type="submit" class="btn btn-info">Add ${quizNumber}</button>
                            </form>
				        </td>
						</#if>
				</tr>
				</#list>
			</tbody>
			<#if courseID??>
					<td>
					<form class="form-inline" method="POST" action="/publishQuiz">
						<input name="courseID" value=${courseID} hidden/>
						<input name="userID" value=${userId} hidden/>
						<div class="form-group">
							<input type="text" name="quizTopic" id="quizTopic" placeholder="Quiz Topic: " required/>
						</div>
						<div class="form-group">
							<input type="text" name="quizTime" id="quizTime" placeholder="Quiz Time: " required/>
						</div>
						<button type="submit" class="btn btn-info">PUBLISH QUIZ ${quizNumber}</button>
					</form>
				</td>
			</#if>
		</table>
	</div>
</body>
</html>