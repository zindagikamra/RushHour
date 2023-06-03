public interface PQHook
{
	void onEnqueue(Object o);
	void onDequeue(Object o);
	void onUpdate(Object oOld, Object oNew);
}