package br.eti.clairton.vraptor.crud.controller;

class Page {
	public final Integer offset;
	public final Integer limit;

	public Page(final Integer offet, final Integer limit) {
		this.offset = offet;
		this.limit = limit;
	}
}