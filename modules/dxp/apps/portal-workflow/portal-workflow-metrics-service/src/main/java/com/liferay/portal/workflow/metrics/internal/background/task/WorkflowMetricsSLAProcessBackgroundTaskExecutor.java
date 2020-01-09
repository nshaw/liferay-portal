/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.internal.background.task;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.BucketAggregationResult;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregationResult;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.workflow.metrics.internal.search.index.SLAInstanceResultWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.internal.search.index.SLATaskResultWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.internal.sla.processor.WorkflowMetricsSLAInstanceResult;
import com.liferay.portal.workflow.metrics.internal.sla.processor.WorkflowMetricsSLAProcessor;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinition;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion;
import com.liferay.portal.workflow.metrics.service.WorkflowMetricsSLADefinitionLocalService;
import com.liferay.portal.workflow.metrics.service.WorkflowMetricsSLADefinitionVersionLocalService;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import java.text.DateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = "background.task.executor.class.name=com.liferay.portal.workflow.metrics.internal.background.task.WorkflowMetricsSLAProcessBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class WorkflowMetricsSLAProcessBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		if (_searchRequestExecutor == null) {
			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_CANCELLED);
		}

		long workflowMetricsSLADefinitionId = MapUtil.getLong(
			backgroundTask.getTaskContextMap(),
			"workflowMetricsSLADefinitionId");

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			_workflowMetricsSLADefinitionLocalService.
				fetchWorkflowMetricsSLADefinition(
					workflowMetricsSLADefinitionId);

		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion =
				_workflowMetricsSLADefinitionVersionLocalService.
					getWorkflowMetricsSLADefinitionVersion(
						workflowMetricsSLADefinitionId,
						workflowMetricsSLADefinition.getVersion());

		long startNodeId = _getStartNodeId(
			workflowMetricsSLADefinition.getCompanyId(),
			workflowMetricsSLADefinition.getProcessId(),
			workflowMetricsSLADefinition.getProcessVersion());

		_processMissingInstances(
			startNodeId, workflowMetricsSLADefinitionVersion);

		_processRunningInstances(
			0, startNodeId, workflowMetricsSLADefinitionVersion);

		_processCompletedInstances(
			startNodeId, workflowMetricsSLADefinitionVersion);

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	private BooleanQuery _createBooleanQuery(
		long companyId, Date createDate, long processId,
		long workflowMetricsSLADefinitionId) {

		BooleanQuery booleanQuery = _queries.booleanQuery();

		BooleanQuery instancesBooleanQuery = _queries.booleanQuery();

		instancesBooleanQuery.addFilterQueryClauses(
			_queries.term("_index", "workflow-metrics-instances"));
		instancesBooleanQuery.addMustQueryClauses(
			_createInstancesBooleanQuery(
				companyId, true, createDate, processId));

		BooleanQuery slaInstanceResultsBooleanQuery = _queries.booleanQuery();

		slaInstanceResultsBooleanQuery.addFilterQueryClauses(
			_queries.term("_index", "workflow-metrics-sla-instance-results"));
		slaInstanceResultsBooleanQuery.addMustNotQueryClauses(
			_queries.term("slaDefinitionId", 0));
		slaInstanceResultsBooleanQuery.addMustQueryClauses(
			_queries.term("companyId", companyId),
			_queries.term("deleted", false),
			_queries.term("processId", processId),
			_queries.term("slaDefinitionId", workflowMetricsSLADefinitionId));

		return booleanQuery.addShouldQueryClauses(
			instancesBooleanQuery, slaInstanceResultsBooleanQuery);
	}

	private BucketSortPipelineAggregation _createBucketSortPipelineAggregation(
		int from) {

		BucketSortPipelineAggregation bucketSortPipelineAggregation =
			_aggregations.bucketSort("bucketSort");

		FieldSort keyFieldSort = _sorts.field("_key");

		keyFieldSort.setSortOrder(SortOrder.ASC);

		bucketSortPipelineAggregation.addSortFields(keyFieldSort);

		bucketSortPipelineAggregation.setFrom(from);
		bucketSortPipelineAggregation.setGapPolicy(GapPolicy.SKIP);
		bucketSortPipelineAggregation.setSize(10000);

		return bucketSortPipelineAggregation;
	}

	private BooleanQuery _createInstancesBooleanQuery(
		long companyId, boolean completed, Date createDate, long processId) {

		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustNotQueryClauses(_queries.term("instanceId", "0"));

		if (createDate != null) {
			RangeTermQuery rangeTermQuery = _queries.rangeTerm(
				"completionDate", true, true);

			rangeTermQuery.setLowerBound(_formatDate(createDate));

			booleanQuery.addMustQueryClauses(rangeTermQuery);
		}

		return booleanQuery.addMustQueryClauses(
			_queries.term("companyId", companyId),
			_queries.term("completed", completed),
			_queries.term("deleted", false),
			_queries.term("processId", processId));
	}

	private BooleanQuery _createInstancesBooleanQuery(
		long companyId, boolean completed, Date createDate, long instanceId,
		long processId) {

		BooleanQuery booleanQuery = _createInstancesBooleanQuery(
			companyId, completed, createDate, processId);

		return booleanQuery.addMustQueryClauses(
			_queries.term("instanceId", instanceId));
	}

	private BooleanQuery _createSLAInstanceResultsBooleanQuery(
		long companyId, long processId, long slaDefinitionId) {

		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustNotQueryClauses(
			_queries.term("slaDefinitionId", "0"),
			_queries.term("status", WorkflowMetricsSLAStatus.COMPLETED.name()));

		return booleanQuery.addMustQueryClauses(
			_queries.term("companyId", companyId),
			_queries.term("deleted", false),
			_queries.term("instanceCompleted", true),
			_queries.term("processId", processId),
			_queries.term("slaDefinitionId", slaDefinitionId));
	}

	private String _formatDate(Date date) {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			_INDEX_DATE_FORMAT_PATTERN);

		try {
			return dateFormat.format(date);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}

			return null;
		}
	}

	private LocalDateTime _getCompletionLocalDateTime(
		long companyId, long instanceId, long processId) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames("workflow-metrics-instances");
		searchSearchRequest.setQuery(
			_createInstancesBooleanQuery(
				companyId, true, null, instanceId, processId));
		searchSearchRequest.setSize(1);

		return Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::parallelStream
		).map(
			SearchHit::getDocument
		).map(
			document -> LocalDateTime.parse(
				document.getString("completionDate"),
				DateTimeFormatter.ofPattern(_INDEX_DATE_FORMAT_PATTERN))
		).findFirst(
		).orElseGet(
			null
		);
	}

	private Map<Long, LocalDateTime> _getCreateLocalDateTimes(

		long companyId, long processId) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames("workflow-metrics-instances");
		searchSearchRequest.setQuery(
			_createInstancesBooleanQuery(companyId, false, null, processId));
		searchSearchRequest.setSize(10000);

		return Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::parallelStream
		).map(
			SearchHit::getDocument
		).collect(
			Collectors.toMap(
				document -> document.getLong("instanceId"),
				document -> LocalDateTime.parse(
					document.getString("createDate"),
					DateTimeFormatter.ofPattern(_INDEX_DATE_FORMAT_PATTERN)))
		);
	}

	private Map<Long, LocalDateTime> _getMissingCreateLocalDateTimes(
		long companyId, Date createDate, long processId,
		long workflowMetricsSLADefinitionId) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			"instanceId", "instanceId");

		termsAggregation.addChildAggregation(_aggregations.topHits("topHits"));
		termsAggregation.setSize(10000);

		searchSearchRequest.addAggregation(termsAggregation);

		searchSearchRequest.setIndexNames(
			"workflow-metrics-instances",
			"workflow-metrics-sla-instance-results");

		searchSearchRequest.setQuery(
			_createBooleanQuery(
				companyId, createDate, processId,
				workflowMetricsSLADefinitionId));

		return Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getAggregationResultsMap
		).map(
			aggregationResultsMap ->
				(TermsAggregationResult)aggregationResultsMap.get("instanceId")
		).map(
			BucketAggregationResult::getBuckets
		).flatMap(
			Collection::stream
		).filter(
			bucket -> bucket.getDocCount() == 1
		).map(
			bucket ->
				(TopHitsAggregationResult)bucket.getChildAggregationResult(
					"topHits")
		).map(
			TopHitsAggregationResult::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::parallelStream
		).map(
			SearchHit::getSourcesMap
		).filter(
			sourcesMap -> GetterUtil.getString(
				sourcesMap.get("uid")
			).startsWith(
				"WorkflowMetricsInstance"
			)
		).collect(
			Collectors.toMap(
				sourcesMap -> GetterUtil.getLong(sourcesMap.get("instanceId")),
				sourcesMap -> LocalDateTime.parse(
					GetterUtil.getString(sourcesMap.get("createDate")),
					DateTimeFormatter.ofPattern(_INDEX_DATE_FORMAT_PATTERN)))
		);
	}

	private long _getStartNodeId(
		long companyId, long processId, String version) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames("workflow-metrics-nodes");

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addMustQueryClauses(
				_queries.term("companyId", companyId),
				_queries.term("deleted", false), _queries.term("initial", true),
				_queries.term("processId", processId),
				_queries.term("version", version)));

		return Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::parallelStream
		).map(
			SearchHit::getDocument
		).findFirst(
		).map(
			document -> document.getLong("nodeId")
		).orElseGet(
			() -> 0L
		);
	}

	private void _indexWorkflowMetricsSLAInstanceResult(
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		_slaInstanceResultWorkflowMetricsIndexer.addDocument(
			_slaInstanceResultWorkflowMetricsIndexer.createDocument(
				workflowMetricsSLAInstanceResult));

		_slaTaskResultWorkflowMetricsIndexer.addDocuments(
			workflowMetricsSLAInstanceResult.
				getWorkflowMetricsSLATaskResults());
	}

	private void _processCompletedInstances(
		long startNodeId,
		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(
			"workflow-metrics-sla-instance-results");
		searchSearchRequest.setQuery(
			_createSLAInstanceResultsBooleanQuery(
				workflowMetricsSLADefinitionVersion.getCompanyId(),
				workflowMetricsSLADefinitionVersion.getProcessId(),
				workflowMetricsSLADefinitionVersion.
					getWorkflowMetricsSLADefinitionId()));
		searchSearchRequest.setSize(10000);

		Stream.of(
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest)
		).map(
			SearchSearchResponse::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::parallelStream
		).map(
			SearchHit::getDocument
		).map(
			document -> _workflowMetricsSLAProcessor.process(
				workflowMetricsSLADefinitionVersion.getCompanyId(), null,
				document.getLong("instanceId"), LocalDateTime.now(),
				startNodeId, workflowMetricsSLADefinitionVersion)
		).map(
			optional -> {
				WorkflowMetricsSLAInstanceResult
					workflowMetricsSLAInstanceResult = optional.get();

				workflowMetricsSLAInstanceResult.setCompletionLocalDateTime(
					_getCompletionLocalDateTime(
						workflowMetricsSLADefinitionVersion.getCompanyId(),
						workflowMetricsSLAInstanceResult.getInstanceId(),
						workflowMetricsSLADefinitionVersion.getProcessId()));

				workflowMetricsSLAInstanceResult.setWorkflowMetricsSLAStatus(
					WorkflowMetricsSLAStatus.COMPLETED);

				return workflowMetricsSLAInstanceResult;
			}
		).forEach(
			this::_indexWorkflowMetricsSLAInstanceResult
		);
	}

	private void _processMissingInstances(
		long startNodeId,
		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion) {

		Map<Long, LocalDateTime> missingCreateLocalDateTimes =
			_getMissingCreateLocalDateTimes(
				workflowMetricsSLADefinitionVersion.getCompanyId(),
				workflowMetricsSLADefinitionVersion.getCreateDate(),
				workflowMetricsSLADefinitionVersion.getProcessId(),
				workflowMetricsSLADefinitionVersion.
					getWorkflowMetricsSLADefinitionId());

		Set<Map.Entry<Long, LocalDateTime>> entrySet =
			missingCreateLocalDateTimes.entrySet();

		Stream<Map.Entry<Long, LocalDateTime>> stream =
			entrySet.parallelStream();

		stream.map(
			entry -> _workflowMetricsSLAProcessor.process(
				workflowMetricsSLADefinitionVersion.getCompanyId(),
				entry.getValue(), entry.getKey(), LocalDateTime.now(),
				startNodeId, workflowMetricsSLADefinitionVersion)
		).filter(
			Optional::isPresent
		).map(
			optional -> {
				WorkflowMetricsSLAInstanceResult
					workflowMetricsSLAInstanceResult = optional.get();

				workflowMetricsSLAInstanceResult.setCompletionLocalDateTime(
					_getCompletionLocalDateTime(
						workflowMetricsSLADefinitionVersion.getCompanyId(),
						workflowMetricsSLAInstanceResult.getInstanceId(),
						workflowMetricsSLADefinitionVersion.getProcessId()));

				workflowMetricsSLAInstanceResult.setWorkflowMetricsSLAStatus(
					WorkflowMetricsSLAStatus.COMPLETED);

				return workflowMetricsSLAInstanceResult;
			}
		).forEach(
			this::_indexWorkflowMetricsSLAInstanceResult
		);
	}

	private void _processRunningInstances(
		int from, long startNodeId,
		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			"instanceId", "instanceId");

		termsAggregation.addChildrenAggregations(
			_aggregations.topHits("topHits"));

		termsAggregation.addPipelineAggregation(
			_createBucketSortPipelineAggregation(from));

		termsAggregation.setSize(10000);

		searchSearchRequest.addAggregation(termsAggregation);

		searchSearchRequest.setIndexNames("workflow-metrics-instances");

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(
				_createInstancesBooleanQuery(
					workflowMetricsSLADefinitionVersion.getCompanyId(), false,
					null, workflowMetricsSLADefinitionVersion.getProcessId())));

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		long count = searchSearchResponse.getCount();

		if (count > 10000) {
			_processRunningInstances(
				from + 10000, startNodeId, workflowMetricsSLADefinitionVersion);
		}

		List<Document> slaInstanceResultDocuments = new ArrayList<>();
		List<Document> slaTaskResultDocuments = new ArrayList<>();

		Stream.of(
			searchSearchResponse.getAggregationResultsMap()
		).map(
			aggregationResultsMap ->
				(TermsAggregationResult)aggregationResultsMap.get("instanceId")
		).map(
			BucketAggregationResult::getBuckets
		).flatMap(
			Collection::stream
		).map(
			bucket ->
				(TopHitsAggregationResult)bucket.getChildAggregationResult(
					"topHits")
		).map(
			TopHitsAggregationResult::getSearchHits
		).map(
			SearchHits::getSearchHits
		).flatMap(
			List::parallelStream
		).map(
			SearchHit::getSourcesMap
		).map(
			sourcesMap -> _workflowMetricsSLAProcessor.process(
				workflowMetricsSLADefinitionVersion.getCompanyId(), null,
				LocalDateTime.parse(
					(String)sourcesMap.get("createDate"),
					DateTimeFormatter.ofPattern(_INDEX_DATE_FORMAT_PATTERN)),
				GetterUtil.getLong(sourcesMap.get("instanceId")),
				LocalDateTime.now(), startNodeId,
				workflowMetricsSLADefinitionVersion)
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).forEach(
			workflowMetricsSLAInstanceResult -> {
				slaInstanceResultDocuments.add(
					_slaInstanceResultWorkflowMetricsIndexer.createDocument(
						workflowMetricsSLAInstanceResult));

				for (WorkflowMetricsSLATaskResult workflowMetricsSLATaskResult :
						workflowMetricsSLAInstanceResult.
							getWorkflowMetricsSLATaskResults()) {

					slaTaskResultDocuments.add(
						_slaTaskResultWorkflowMetricsIndexer.createDocument(
							workflowMetricsSLATaskResult));
				}
			}
		);

		_slaInstanceResultWorkflowMetricsIndexer.addDocuments(
			slaInstanceResultDocuments);
		_slaTaskResultWorkflowMetricsIndexer.addDocuments(
			slaTaskResultDocuments);
	}

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
		PropsKeys.INDEX_DATE_FORMAT_PATTERN);

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowMetricsSLAProcessBackgroundTaskExecutor.class);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private Queries _queries;

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(search.engine.impl=Elasticsearch)"
	)
	private volatile SearchRequestExecutor _searchRequestExecutor;

	@Reference
	private SLAInstanceResultWorkflowMetricsIndexer
		_slaInstanceResultWorkflowMetricsIndexer;

	@Reference
	private SLATaskResultWorkflowMetricsIndexer
		_slaTaskResultWorkflowMetricsIndexer;

	@Reference
	private Sorts _sorts;

	@Reference
	private WorkflowMetricsSLADefinitionLocalService
		_workflowMetricsSLADefinitionLocalService;

	@Reference
	private WorkflowMetricsSLADefinitionVersionLocalService
		_workflowMetricsSLADefinitionVersionLocalService;

	@Reference
	private WorkflowMetricsSLAProcessor _workflowMetricsSLAProcessor;

}