import router from '../../router'
import { ServiceFactory } from '../../services/ServiceFactory';
const recensionService = ServiceFactory.get('recension');


const state = {
    comments: [],
};

const getters = {
    comments: state => state.comments,
};

const actions = {
    async fetchComments({ commit }) {

        let response;
        try {
            response = await recensionService.getPendingComments();
            commit('fetchComments', response.data);
        } catch (e) { }
    },

};

const mutations = {
    fetchComments: (state, data) => {
        state.comments = data;
    },
};

export default {
    state,
    getters,
    actions,
    mutations
}